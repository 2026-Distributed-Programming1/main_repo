package com.insurance.test.integration;

import com.insurance.actor.ClaimsHandler;
import com.insurance.actor.Customer;
import com.insurance.actor.DispatchAgent;
import com.insurance.claim.AccidentReport;
import com.insurance.claim.ClaimCalculation;
import com.insurance.claim.ClaimPayment;
import com.insurance.claim.ClaimRequest;
import com.insurance.claim.DamageInvestigation;
import com.insurance.claim.Dispatch;
import com.insurance.claim.DispatchRecord;
import com.insurance.common.Attachment;
import com.insurance.common.BankAccount;
import com.insurance.contract.InsuranceContract;
import com.insurance.enums.AccidentReportStatus;
import com.insurance.enums.AccidentType;
import com.insurance.enums.AuthMethod;
import com.insurance.enums.CalculationStatus;
import com.insurance.enums.ClaimPaymentStatus;
import com.insurance.enums.ClaimRequestStatus;
import com.insurance.enums.ClaimType;
import com.insurance.enums.DispatchRecordStatus;
import com.insurance.enums.DispatchStatus;
import com.insurance.enums.InvestigationResult;
import com.insurance.enums.InvestigationStatus;
import com.insurance.enums.NoticeMethod;
import com.insurance.enums.PaymentType;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * 7️⃣ 사고/보험금 도메인 통합 테스트
 *
 * 시나리오 전체 흐름이 일관되게 동작하는지 검증한다:
 * 사고 접수 → 현장 출동 → 현장 기록 → 보험금 청구 → 손해 조사 → 보험금 산출 → 보험금 지급
 *
 * 각 단계가 다음 단계로 이어지는 객체 참조 관계와 상태 전이를 통합적으로 검증한다.
 */
public class ClaimFlowIntegrationTest {

    private Customer customer;
    private InsuranceContract contract;
    private DispatchAgent agent;
    private ClaimsHandler handler;
    private BankAccount account;

    @Before
    public void setUp() {
        customer = new Customer("통합고객", "900101-1234567", "010-1111-2222", "test@test.com");
        account = new BankAccount();
        account.enter("국민은행", "123-456", "통합고객");
        account.verify();
        customer.registerAccount(account);

        contract = new InsuranceContract(customer,
                LocalDate.now().minusYears(2), LocalDate.now().plusYears(8), 500_000L);

        agent = new DispatchAgent("이출동", "현장출동팀", "사원", "강남", "12가3456");
        handler = new ClaimsHandler("박보상", "보상팀", "대리", 5_000_000L);
    }

    @Test
    public void UC1_to_UC6_전체_보험금_처리_흐름() {
        // ===== UC1: 사고 접수 =====
        AccidentReport report = new AccidentReport(customer);
        report.enterVehicleInfo("12가3456", "통합고객", "010-1111-2222");
        report.selectAccidentType(AccidentType.OBJECT, "차량 파손");
        report.enterLocation("서울시 강남구");
        report.agreeTerms();
        report.setDispatchOption(true);
        report.receive();

        assertEquals(AccidentReportStatus.RECEIVED, report.getStatus());

        // ===== UC2-1: 현장 출동 =====
        Dispatch dispatch = report.requestDispatch();
        assertNotNull(dispatch);
        assertEquals(report, dispatch.getAccident());

        dispatch.assignAgent(agent);
        dispatch.depart();
        dispatch.arrive();
        assertEquals(DispatchStatus.ARRIVED, dispatch.getStatus());

        // ===== UC2-2: 현장 출동 기록 =====
        DispatchRecord record = new DispatchRecord(dispatch);
        record.uploadPhoto("전경", new Attachment(new File("front.jpg")));
        record.uploadPhoto("파손", new Attachment(new File("damage.jpg")));
        record.setPoliceRequired(false);
        record.setTowingRequired(true);
        record.enterNotes("후방 추돌 사고");
        record.transmit();
        dispatch.complete();

        assertEquals(DispatchRecordStatus.TRANSMITTED, record.getStatus());
        assertEquals(DispatchStatus.COMPLETED, dispatch.getStatus());

        // ===== UC3: 보험금 청구 =====
        ClaimRequest claim = new ClaimRequest(customer, contract);
        claim.agreePersonalInfoTerms();
        claim.selectAuthMethod(AuthMethod.MOBILE);
        claim.authenticate();
        claim.confirmRecipientInfo();
        claim.selectInsured(customer);
        claim.selectClaimType(ClaimType.ACCIDENT);
        claim.selectClaimReasons(Arrays.asList("입원", "수술"));
        claim.enterDiagnosis("경추 염좌");
        claim.selectExistingAccount(account);
        claim.verifyAccount();
        claim.selectNoticeMethod(NoticeMethod.KAKAO);
        claim.attachDocument(new Attachment(new File("진단서.pdf")));
        claim.submit();

        assertEquals(ClaimRequestStatus.RECEIVED, claim.getStatus());
        assertEquals(contract, claim.getContract());

        // ===== UC4: 손해 조사 =====
        DamageInvestigation investigation = new DamageInvestigation(claim);
        investigation.assignHandler(handler);
        investigation.enterRecognizedDamage(3_000_000L);
        investigation.enterFaultRatio(70.0, 30.0);
        investigation.enterOpinion("후방 추돌 사고로 우리 고객 70% 과실");
        investigation.selectResult(InvestigationResult.APPROVED);

        assertTrue(investigation.validateFaultRatio());

        ClaimCalculation calculation = investigation.complete();
        assertNotNull(calculation);
        assertEquals(InvestigationStatus.INVESTIGATED, investigation.getStatus());
        assertEquals(claim, calculation.getInvestigation().getClaim());

        // ===== UC5: 보험금 산출 =====
        // 손해액 3,000,000 × 70% - 자기부담금 100,000 = 2,000,000원
        assertEquals(2_000_000L, calculation.getFinalAmount());
        assertEquals(CalculationStatus.CALCULATED, calculation.getStatus());
        assertFalse(calculation.isExceededDeductible());
        assertFalse(calculation.isAdjusted());

        // 전결 한도 내(2,000,000 < 5,000,000) → 즉시 승인
        ClaimPayment payment = calculation.approve();
        assertNotNull(payment);
        assertEquals(CalculationStatus.APPROVED, calculation.getStatus());

        // ===== UC6: 보험금 지급 =====
        assertEquals(2_000_000L, payment.getFinalAmount());
        assertEquals(account, payment.getAccount());

        payment.selectPaymentType(PaymentType.IMMEDIATE);
        payment.setNoticeOption(Collections.singletonList(NoticeMethod.KAKAO));
        payment.enterOTP("123456");
        payment.verifyOTP();
        payment.execute();
        payment.sendCompletionNotice();
        payment.close();

        assertEquals(ClaimPaymentStatus.CLOSED, payment.getStatus());
        assertNotNull(payment.getPaidAt());
        assertTrue(payment.isNoticeSent());
    }

    @Test
    public void E1_가입내역_미확인_시_접수_실패() {
        // null 고객으로 시작 (가입 미확인 시뮬레이션)
        AccidentReport report = new AccidentReport(null);
        report.enterVehicleInfo("12가3456", "이름", "010-1111-2222");
        report.selectAccidentType(AccidentType.OBJECT, "파손");
        report.enterLocation("서울");
        report.agreeTerms();

        assertFalse(report.verifyContract());
        // verifyContract가 실패하면 receive()가 호출되어도 상태가 RECEIVED로 안 바뀜
        report.receive();
        assertNotEquals(AccidentReportStatus.RECEIVED, report.getStatus());
    }

    @Test
    public void A2_재해청구_사고상세포함_흐름() {
        ClaimRequest claim = new ClaimRequest(customer, contract);
        claim.agreePersonalInfoTerms();
        claim.selectAuthMethod(AuthMethod.MOBILE);
        claim.authenticate();
        claim.confirmRecipientInfo();
        claim.selectInsured(customer);
        claim.selectClaimType(ClaimType.ACCIDENT);

        // 재해이므로 사고 상세 입력
        com.insurance.claim.AccidentDetail detail = new com.insurance.claim.AccidentDetail();
        detail.enter(com.insurance.enums.AccidentSubType.TRAFFIC,
                "후방 추돌", LocalDate.of(2025, 4, 15), "강남대로");
        claim.enterAccidentDetail(detail);

        claim.selectClaimReasons(Arrays.asList("입원"));
        claim.enterDiagnosis("경추 염좌");
        claim.selectExistingAccount(account);
        claim.verifyAccount();
        claim.selectNoticeMethod(NoticeMethod.KAKAO);
        claim.submit();

        assertEquals(ClaimRequestStatus.RECEIVED, claim.getStatus());
        assertNotNull(claim.getAccidentDetail());
    }

    @Test
    public void A1_결재상신_흐름_고액_산출_시() {
        // 손해액을 크게 설정하여 전결 한도(5,000,000) 초과 유도
        ClaimRequest claim = createSimpleClaim();
        DamageInvestigation inv = new DamageInvestigation(claim);
        inv.assignHandler(handler);
        inv.enterRecognizedDamage(20_000_000L);
        inv.enterFaultRatio(100.0, 0.0);
        inv.enterOpinion("의견");
        inv.selectResult(InvestigationResult.APPROVED);

        ClaimCalculation calc = inv.complete();
        assertNotNull(calc);
        // 산출액: 20,000,000 - 100,000 = 19,900,000원
        assertEquals(19_900_000L, calc.getFinalAmount());

        // 전결 한도(5M) 초과 → 결재 상신 필요
        assertTrue(calc.getFinalAmount() > handler.getTransferLimit());

        ClaimsHandler approver = new ClaimsHandler("정보상", "보상팀", "과장", 50_000_000L);
        calc.selectApprover(approver);
        calc.submitForApproval();

        assertEquals(CalculationStatus.APPROVAL_PENDING, calc.getStatus());
        assertEquals(approver, calc.getApprover());
    }

    private ClaimRequest createSimpleClaim() {
        ClaimRequest claim = new ClaimRequest(customer, contract);
        claim.agreePersonalInfoTerms();
        claim.selectAuthMethod(AuthMethod.MOBILE);
        claim.authenticate();
        claim.confirmRecipientInfo();
        claim.selectInsured(customer);
        claim.selectClaimType(ClaimType.DISEASE);
        claim.selectClaimReasons(Arrays.asList("입원"));
        claim.enterDiagnosis("감기");
        claim.selectExistingAccount(account);
        claim.verifyAccount();
        claim.selectNoticeMethod(NoticeMethod.KAKAO);
        claim.submit();
        return claim;
    }
}
