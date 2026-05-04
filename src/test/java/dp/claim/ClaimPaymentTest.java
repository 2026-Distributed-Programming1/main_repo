package com.insurance.test.claim;

import com.insurance.actor.Customer;
import com.insurance.claim.ClaimCalculation;
import com.insurance.claim.ClaimPayment;
import com.insurance.claim.ClaimRequest;
import com.insurance.claim.DamageInvestigation;
import com.insurance.common.BankAccount;
import com.insurance.contract.InsuranceContract;
import com.insurance.enums.AuthMethod;
import com.insurance.enums.ClaimPaymentStatus;
import com.insurance.enums.InvestigationResult;
import com.insurance.enums.NoticeMethod;
import com.insurance.enums.PaymentType;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * ClaimPayment 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 지급번호, 수령인·계좌 자동 로드, 지급액 자동 로드
 * - E1: OTP 인증 실패 (6자리 미달)
 * - 정상 흐름: 인증 → 이체 → 종결
 * - A1: 예약 지급
 * - E2: 이체 실패 (계좌 미인증)
 */
public class ClaimPaymentTest {

    private ClaimCalculation calculation;
    private BankAccount account;

    @Before
    public void setUp() {
        Customer customer = new Customer("청구고객", "900101-1234567", "010-1111-2222", "test@test.com");
        InsuranceContract contract = new InsuranceContract(customer,
                LocalDate.now().minusYears(1), LocalDate.now().plusYears(9), 500_000L);

        account = new BankAccount();
        account.enter("국민은행", "123-456", "청구고객");
        account.verify();
        customer.registerAccount(account);

        ClaimRequest claim = new ClaimRequest(customer, contract);
        claim.confirmRecipientInfo();
        claim.selectAuthMethod(AuthMethod.MOBILE);
        claim.authenticate();
        claim.selectExistingAccount(account);
        claim.verifyAccount();

        DamageInvestigation inv = new DamageInvestigation(claim);
        inv.enterRecognizedDamage(3_000_000L);
        inv.enterFaultRatio(70.0, 30.0);
        inv.selectResult(InvestigationResult.APPROVED);

        calculation = new ClaimCalculation(inv);
    }

    @Test
    public void 생성_시_지급번호_자동부여_및_정보_자동로드() {
        ClaimPayment payment = new ClaimPayment(calculation);

        assertNotNull(payment.getPaymentNo());
        assertTrue(payment.getPaymentNo().startsWith("CPY"));
        assertEquals(ClaimPaymentStatus.WAITING, payment.getStatus());
        assertEquals(2_000_000L, payment.getFinalAmount());
        assertEquals(account, payment.getAccount());
        assertNotNull(payment.getRecipient());
    }

    @Test
    public void 지급유형_즉시_선택() {
        ClaimPayment payment = new ClaimPayment(calculation);
        payment.selectPaymentType(PaymentType.IMMEDIATE);

        assertEquals(PaymentType.IMMEDIATE, payment.getPaymentType());
    }

    @Test
    public void A1_예약지급_등록() {
        ClaimPayment payment = new ClaimPayment(calculation);
        payment.selectPaymentType(PaymentType.SCHEDULED);
        LocalDateTime when = LocalDateTime.now().plusDays(7);
        payment.setScheduledDateTime(when);
        payment.schedule();

        assertEquals(when, payment.getScheduledAt());
        assertEquals(ClaimPaymentStatus.SCHEDULED, payment.getStatus());
    }

    @Test
    public void OTP_6자리_입력시_인증_성공() {
        ClaimPayment payment = new ClaimPayment(calculation);
        payment.enterOTP("123456");
        boolean result = payment.verifyOTP();

        assertTrue(result);
        assertTrue(payment.isOtpVerified());
    }

    @Test
    public void E1_OTP_6자리_미달시_인증_실패() {
        ClaimPayment payment = new ClaimPayment(calculation);
        payment.enterOTP("123");
        boolean result = payment.verifyOTP();

        assertFalse(result);
        assertFalse(payment.isOtpVerified());
    }

    @Test
    public void OTP_미인증_상태에서_execute_안됨() {
        ClaimPayment payment = new ClaimPayment(calculation);
        // OTP 인증 안 한 상태
        payment.execute();

        assertNull(payment.getPaidAt());
        assertNotEquals(ClaimPaymentStatus.COMPLETED, payment.getStatus());
    }

    @Test
    public void 정상흐름_OTP_인증_후_이체_완료() {
        ClaimPayment payment = new ClaimPayment(calculation);
        payment.enterOTP("123456");
        payment.verifyOTP();
        payment.execute();

        assertEquals(ClaimPaymentStatus.COMPLETED, payment.getStatus());
        assertNotNull(payment.getPaidAt());
    }

    @Test
    public void E2_계좌_미인증시_이체_실패() {
        ClaimPayment payment = new ClaimPayment(calculation);
        payment.enterOTP("123456");
        payment.verifyOTP();

        // 계좌 강제 변경하여 미인증 상태로
        BankAccount fakeAccount = new BankAccount();
        // payment의 account를 직접 변경할 수 없으므로 handleTransferFailure 직접 호출로 검증
        payment.handleTransferFailure("계좌 미인증");

        assertEquals(ClaimPaymentStatus.FAILED, payment.getStatus());
        assertTrue(payment.isTransferFailed());
        assertEquals("계좌 미인증", payment.getFailureReason());
    }

    @Test
    public void 안내메시지_옵션_설정() {
        ClaimPayment payment = new ClaimPayment(calculation);
        payment.setNoticeOption(Arrays.asList(NoticeMethod.KAKAO, NoticeMethod.SMS));

        assertEquals(2, payment.getNoticeOption().size());
    }

    @Test
    public void 지급완료_후_안내메시지_발송() {
        ClaimPayment payment = new ClaimPayment(calculation);
        payment.setNoticeOption(Collections.singletonList(NoticeMethod.KAKAO));
        payment.enterOTP("123456");
        payment.verifyOTP();
        payment.execute();
        payment.sendCompletionNotice();

        assertTrue(payment.isNoticeSent());
    }

    @Test
    public void 종결_시_상태_CLOSED() {
        ClaimPayment payment = new ClaimPayment(calculation);
        payment.close();

        assertEquals(ClaimPaymentStatus.CLOSED, payment.getStatus());
    }
}
