package dp.claim;

import static org.junit.jupiter.api.Assertions.*;

import dp.actor.ClaimsHandler;
import dp.actor.Customer;
import dp.contract.Contract;
import dp.enums.CalculationStatus;
import dp.enums.InvestigationResult;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * ClaimCalculation 단위 테스트
 *
 * 검증 대상:
 * - 생성자에서 자동 산출
 * - 정상 흐름: 산출 → 승인 → ClaimPayment 반환
 * - E1: 자기부담금 초과 (적용 손해액 ≤ 자기부담금)
 * - E2: 보장 한도 초과 시 자동 조정
 * - A1: 결재 상신
 */
public class ClaimCalculationTest {

    private DamageInvestigation investigation;

    @BeforeEach
    public void setUp() {
        Customer customer = new Customer("테스트고객", "900101-1234567", "010-0000-0000", "test@test.com");
        Contract contract = new Contract(customer,
                LocalDate.now().minusYears(1), LocalDate.now().plusYears(9), 500_000L);
        ClaimRequest claim = new ClaimRequest(customer, contract);
        investigation = new DamageInvestigation(claim);
    }

    @Test
    public void 생성_시_산출번호_자동부여_및_calculate_자동호출() {
        investigation.enterRecognizedDamage(3_000_000L);
        investigation.enterFaultRatio(70.0, 30.0);
        investigation.selectResult(InvestigationResult.APPROVED);

        ClaimCalculation calc = new ClaimCalculation(investigation);

        assertNotNull(calc.getCalculationNo());
        assertTrue(calc.getCalculationNo().startsWith("CAL"));
        assertNotNull(calc.getCalculatedAt());
        assertEquals(CalculationStatus.CALCULATED, calc.getStatus());
    }

    @Test
    public void 정상_산출액_계산_검증() {
        // 손해액 3,000,000원 × 우리 과실 70% - 자기부담금 100,000원 = 2,000,000원
        investigation.enterRecognizedDamage(3_000_000L);
        investigation.enterFaultRatio(70.0, 30.0);
        investigation.selectResult(InvestigationResult.APPROVED);

        ClaimCalculation calc = new ClaimCalculation(investigation);

        assertEquals(2_000_000L, calc.getFinalAmount());
    }

    @Test
    public void E1_적용손해액이_자기부담금_이하면_초과여부_true() {
        // 손해액 100,000원 × 50% = 50,000원, 자기부담금 100,000원 미달
        investigation.enterRecognizedDamage(100_000L);
        investigation.enterFaultRatio(50.0, 50.0);
        investigation.selectResult(InvestigationResult.APPROVED);

        ClaimCalculation calc = new ClaimCalculation(investigation);

        assertTrue(calc.isExceededDeductible());
        // 산출액은 0원
        assertEquals(0L, calc.getFinalAmount());
    }

    @Test
    public void E1_공제액_초과_종결처리() {
        investigation.enterRecognizedDamage(50_000L);
        investigation.enterFaultRatio(50.0, 50.0);
        investigation.selectResult(InvestigationResult.APPROVED);

        ClaimCalculation calc = new ClaimCalculation(investigation);
        calc.closeAsExceeded();

        assertEquals(CalculationStatus.CLOSED, calc.getStatus());
    }

    @Test
    public void E2_보장한도_초과시_한도까지_자동조정() {
        // 손해액 200,000,000원 × 100% = 200,000,000원, 한도 100,000,000원
        investigation.enterRecognizedDamage(200_000_000L);
        investigation.enterFaultRatio(100.0, 0.0);
        investigation.selectResult(InvestigationResult.APPROVED);

        ClaimCalculation calc = new ClaimCalculation(investigation);

        assertTrue(calc.isAdjusted());
        assertEquals(100_000_000L, calc.getFinalAmount());
    }

    @Test
    public void A1_결재상신_시_상태_APPROVAL_PENDING() {
        investigation.enterRecognizedDamage(10_000_000L);
        investigation.enterFaultRatio(100.0, 0.0);
        investigation.selectResult(InvestigationResult.APPROVED);

        ClaimCalculation calc = new ClaimCalculation(investigation);
        ClaimsHandler approver = new ClaimsHandler("정보상", "보상팀", "과장", 20_000_000L);
        calc.selectApprover(approver);
        calc.submitForApproval();

        assertEquals(approver, calc.getApprover());
        assertTrue(calc.isApprovalRequired());
        assertEquals(CalculationStatus.APPROVAL_PENDING, calc.getStatus());
    }

    @Test
    public void approve_시_ClaimPayment_반환() {
        investigation.enterRecognizedDamage(3_000_000L);
        investigation.enterFaultRatio(70.0, 30.0);
        investigation.selectResult(InvestigationResult.APPROVED);

        ClaimCalculation calc = new ClaimCalculation(investigation);
        ClaimPayment payment = calc.approve();

        assertNotNull(payment);
        assertEquals(CalculationStatus.APPROVED, calc.getStatus());
        assertEquals(calc, payment.getCalculation());
    }
}
