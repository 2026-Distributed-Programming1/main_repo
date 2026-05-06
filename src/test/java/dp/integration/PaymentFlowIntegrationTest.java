package dp.integration;

import dp.actor.Customer;
import dp.common.BankAccount;
import dp.contract.Cancellation;
import dp.contract.Contract;
import dp.enums.PaymentMethod;
import dp.enums.PaymentStatus;
import dp.enums.RefundPaymentStatus;
import dp.enums.RefundStatus;
import dp.payment.Payment;
import dp.payment.PaymentItem;
import dp.payment.RefundCalculation;
import dp.payment.RefundPayment;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 8️⃣ 납입/환급 도메인 통합 테스트
 *
 * 시나리오 흐름이 일관되게 동작하는지 검증한다:
 * 1. 보험료 납입 (UC7) - N:M 매핑 검증
 * 2. 해약 환급금 산출 (UC9) → 환급금 지급 (UC10) 흐름
 */
public class PaymentFlowIntegrationTest {

    private Customer customer;
    private Contract contract1;
    private Contract contract2;
    private BankAccount account;

    @BeforeEach
    public void setUp() {
        customer = new Customer("통합고객", "900101-1234567", "010-0000-0000", "test@test.com");
        account = new BankAccount();
        account.enter("국민은행", "123-456", "통합고객");
        account.verify();
        customer.registerAccount(account);

        contract1 = new Contract(customer,
                LocalDate.now().minusYears(2), LocalDate.now().plusYears(8), 100_000L);
        contract2 = new Contract(customer,
                LocalDate.now().minusMonths(6), LocalDate.now().plusYears(9), 200_000L);
    }

    @Test
    public void UC7_보험료_납입_NM매핑_정상_흐름() {
        // 한 번의 납입에서 두 계약을 동시에 처리 (N:M)
        Payment payment = new Payment(customer);
        payment.selectContracts(Arrays.asList(contract1, contract2));

        assertEquals(2, payment.getItems().size());

        // 계약별 다른 횟수
        // contract1: 100,000 × 3회 = 300,000원
        // contract2: 200,000 × 1회 = 200,000원
        // 총 500,000원, 3회분 납입 → 선납 할인 1% = 5,000원
        // 최종 495,000원
        payment.enterPaymentCount(payment.getItems().get(0), 3);
        payment.enterPaymentCount(payment.getItems().get(1), 1);

        payment.selectPaymentMethod(PaymentMethod.IMMEDIATE_TRANSFER);
        payment.selectExistingAccount(account);
        payment.calculateTotal();

        assertEquals(500_000L, payment.getTotalAmount());
        assertEquals(5_000L, payment.getEarlyDiscount());
        assertEquals(495_000L, payment.getDiscountedAmount());

        payment.submit();

        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
        assertNotNull(payment.getRequestedAt());

        // PaymentItem이 각각 자기 contract와 매핑되어 있음 확인
        for (PaymentItem item : payment.getItems()) {
            assertNotNull(item.getContract());
            assertEquals(payment, item.getPayment());
        }
    }

    @Test
    public void UC9_to_UC10_환급금_산출_지급_흐름() {
        // ===== 해지 =====
        Cancellation cancellation = new Cancellation(contract1);
        cancellation.calculateExpectedRefund();
        cancellation.confirm();

        assertNotNull(cancellation.getCanceledAt());

        // ===== UC9: 환급금 산출 =====
        RefundCalculation refund = new RefundCalculation(cancellation);

        assertEquals(RefundStatus.CALCULATED, refund.getStatus());
        assertTrue(refund.getFinalRefund() > 0);
        long expectedRefund = refund.getFinalRefund();

        // 환급금 확정 → 지급 이관
        RefundPayment payment = refund.confirm();
        assertNotNull(payment);
        assertEquals(RefundStatus.PAID, refund.getStatus());

        // ===== UC10: 환급금 지급 =====
        assertEquals(expectedRefund, payment.getFinalAmount());
        assertEquals(RefundPaymentStatus.WAITING, payment.getStatus());
        assertEquals(account, payment.getAccount());

        // OTP 인증 → 이체 → 알림톡
        payment.enterOTP("123456");
        payment.verifyOTP();
        payment.execute();
        payment.sendNotice();

        assertEquals(RefundPaymentStatus.COMPLETED, payment.getStatus());
        assertNotNull(payment.getTransferredAt());
        assertTrue(payment.isNoticeSent());
    }

    @Test
    public void A1_공제_조정_후_재산출_흐름() {
        Cancellation cancellation = new Cancellation(contract1);
        cancellation.calculateExpectedRefund();
        cancellation.confirm();

        RefundCalculation refund = new RefundCalculation(cancellation);
        long beforeRefund = refund.getFinalRefund();

        // 공제 조정 (예: 미납 보험료 50,000원 추가 공제)
        refund.adjustDeduction("미납 보험료", 50_000L, "최근 미납 발견");
        refund.recalculate();

        // 환급금이 50,000원 줄어야 함
        assertEquals(beforeRefund - 50_000L, refund.getFinalRefund());
        assertEquals(1, refund.getAdjustments().size());
    }

    @Test
    public void E1_OTP_5회_실패_후_지급_불가() {
        Cancellation cancellation = new Cancellation(contract1);
        cancellation.calculateExpectedRefund();
        cancellation.confirm();

        RefundCalculation refund = new RefundCalculation(cancellation);
        RefundPayment payment = refund.confirm();

        // OTP 5회 실패
        for (int i = 0; i < 5; i++) {
            payment.enterOTP("xxx");
            payment.verifyOTP();
        }

        assertTrue(payment.isLocked());
        assertEquals(RefundPaymentStatus.LOCKED, payment.getStatus());

        // 잠긴 후 정상 OTP 입력해도 인증 안 됨
        payment.enterOTP("123456");
        assertFalse(payment.verifyOTP());

        // execute() 호출해도 이체 안 됨
        payment.execute();
        assertNull(payment.getTransferredAt());
    }

    @Test
    public void NM매핑_단일_계약_납입_시_선납할인_없음() {
        // 1회분만 납입 → 선납 할인 없음
        Payment payment = new Payment(customer);
        payment.selectContracts(Collections.singletonList(contract1));
        payment.enterPaymentCount(payment.getItems().get(0), 1);
        payment.selectPaymentMethod(PaymentMethod.IMMEDIATE_TRANSFER);
        payment.selectExistingAccount(account);
        payment.calculateTotal();

        assertEquals(100_000L, payment.getTotalAmount());
        assertEquals(0L, payment.getEarlyDiscount());
        assertEquals(100_000L, payment.getDiscountedAmount());
    }
}
