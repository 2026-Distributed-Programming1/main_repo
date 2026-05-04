package com.insurance.test.payment;

import com.insurance.actor.Customer;
import com.insurance.common.BankAccount;
import com.insurance.contract.Cancellation;
import com.insurance.contract.InsuranceContract;
import com.insurance.enums.RefundPaymentStatus;
import com.insurance.payment.RefundCalculation;
import com.insurance.payment.RefundPayment;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * RefundPayment 단위 테스트
 *
 * 검증 대상:
 * - 생성 시 자동 로드 (지급액·계좌)
 * - 정상 OTP 인증 → 이체
 * - E1: OTP 5회 실패 시 잠금
 * - E2: 이체 실패 처리
 * - E3: 알림톡 발송 실패 처리
 */
public class RefundPaymentTest {

    private RefundCalculation refund;
    private BankAccount account;

    @Before
    public void setUp() {
        Customer customer = new Customer("테스트", "900101-1234567", "010-0000-0000", "test@test.com");
        account = new BankAccount();
        account.enter("국민은행", "123-456", "테스트");
        account.verify();
        customer.registerAccount(account);

        InsuranceContract contract = new InsuranceContract(customer,
                LocalDate.now().minusYears(2), LocalDate.now().plusYears(8), 100_000L);
        Cancellation cancellation = new Cancellation(contract);
        cancellation.calculateExpectedRefund();
        cancellation.confirm();

        refund = new RefundCalculation(cancellation);
    }

    @Test
    public void 생성_시_지급번호_자동부여_및_계좌_금액_자동로드() {
        RefundPayment payment = new RefundPayment(refund);

        assertNotNull(payment.getPaymentNo());
        assertTrue(payment.getPaymentNo().startsWith("RPY"));
        assertEquals(RefundPaymentStatus.WAITING, payment.getStatus());
        assertEquals(refund.getFinalRefund(), payment.getFinalAmount());
        assertEquals(account, payment.getAccount());
        assertFalse(payment.isLocked());
        assertEquals(0, payment.getOtpFailCount());
    }

    @Test
    public void 정상_OTP_인증() {
        RefundPayment payment = new RefundPayment(refund);
        payment.enterOTP("123456");
        boolean result = payment.verifyOTP();

        assertTrue(result);
        assertTrue(payment.isOtpVerified());
        assertFalse(payment.isLocked());
    }

    @Test
    public void OTP_실패_시_횟수_증가() {
        RefundPayment payment = new RefundPayment(refund);

        payment.enterOTP("123");
        payment.verifyOTP();
        assertEquals(1, payment.getOtpFailCount());

        payment.enterOTP("456");
        payment.verifyOTP();
        assertEquals(2, payment.getOtpFailCount());
    }

    @Test
    public void E1_OTP_5회_실패_시_잠금() {
        RefundPayment payment = new RefundPayment(refund);

        for (int i = 0; i < 5; i++) {
            payment.enterOTP("xxx");
            payment.verifyOTP();
        }

        assertTrue(payment.isLocked());
        assertEquals(RefundPaymentStatus.LOCKED, payment.getStatus());
        assertEquals(5, payment.getOtpFailCount());
    }

    @Test
    public void E1_잠긴_상태에서_OTP_검증_실패() {
        RefundPayment payment = new RefundPayment(refund);

        // 5회 실패로 잠금
        for (int i = 0; i < 5; i++) {
            payment.enterOTP("xxx");
            payment.verifyOTP();
        }

        // 잠긴 후 정상 OTP를 넣어도 인증 안 됨
        payment.enterOTP("123456");
        boolean result = payment.verifyOTP();

        assertFalse(result);
        assertFalse(payment.isOtpVerified());
    }

    @Test
    public void 정상_이체_실행() {
        RefundPayment payment = new RefundPayment(refund);
        payment.enterOTP("123456");
        payment.verifyOTP();
        payment.execute();

        assertEquals(RefundPaymentStatus.COMPLETED, payment.getStatus());
        assertNotNull(payment.getTransferredAt());
    }

    @Test
    public void OTP_미인증_상태에서_execute_안됨() {
        RefundPayment payment = new RefundPayment(refund);
        payment.execute();

        assertNull(payment.getTransferredAt());
        assertNotEquals(RefundPaymentStatus.COMPLETED, payment.getStatus());
    }

    @Test
    public void E2_이체_실패_처리() {
        RefundPayment payment = new RefundPayment(refund);
        payment.handleTransferFailure();

        assertEquals(RefundPaymentStatus.FAILED, payment.getStatus());
    }

    @Test
    public void E3_알림톡_발송_실패_처리() {
        RefundPayment payment = new RefundPayment(refund);
        payment.handleNoticeFailure();

        assertNotNull(payment.getNoticeFailureMessage());
    }

    @Test
    public void 정상흐름_이체_후_알림톡_발송() {
        RefundPayment payment = new RefundPayment(refund);
        payment.enterOTP("123456");
        payment.verifyOTP();
        payment.execute();
        payment.sendNotice();

        assertTrue(payment.isNoticeSent());
    }
}
