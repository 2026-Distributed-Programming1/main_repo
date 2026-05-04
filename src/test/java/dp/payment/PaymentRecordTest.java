package com.insurance.test.payment;

import com.insurance.actor.Customer;
import com.insurance.contract.InsuranceContract;
import com.insurance.enums.PaymentRecordStatus;
import com.insurance.enums.RejectCategory;
import com.insurance.payment.PaymentRecord;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * PaymentRecord 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 결제번호, 결제일자=today, 초기상태 WAITING
 * - 정상 흐름: confirm → COMPLETED + confirmedAt
 * - A3: 반려 사유 입력 후 reject → REJECTED
 * - 반려 사유 미입력 시 reject 안 됨
 */
public class PaymentRecordTest {

    private InsuranceContract contract;

    @Before
    public void setUp() {
        Customer customer = new Customer("테스트", "900101-1234567", "010-0000-0000", "test@test.com");
        contract = new InsuranceContract(customer,
                LocalDate.now().minusYears(1), LocalDate.now().plusYears(9), 100_000L);
    }

    @Test
    public void 생성_시_결제번호와_초기값_설정() {
        PaymentRecord record = new PaymentRecord(contract, 100_000L, "카드");

        assertNotNull(record.getRecordNo());
        assertTrue(record.getRecordNo().startsWith("PRC"));
        assertEquals(LocalDate.now(), record.getPaymentDate());
        assertEquals(100_000L, record.getAmount());
        assertEquals("카드", record.getMethod());
        assertEquals(PaymentRecordStatus.WAITING, record.getStatus());
    }

    @Test
    public void confirm_시_상태_COMPLETED_및_확정시각_설정() {
        PaymentRecord record = new PaymentRecord(contract, 100_000L, "카드");
        record.confirm();

        assertEquals(PaymentRecordStatus.COMPLETED, record.getStatus());
        assertNotNull(record.getConfirmedAt());
    }

    @Test
    public void A3_반려_사유_입력_후_reject_시_상태_REJECTED() {
        PaymentRecord record = new PaymentRecord(contract, 100_000L, "카드");
        record.enterRejectInfo(RejectCategory.DUPLICATE_PAYMENT, "이미 납부된 건");
        record.reject();

        assertEquals(PaymentRecordStatus.REJECTED, record.getStatus());
        assertEquals(RejectCategory.DUPLICATE_PAYMENT, record.getRejectCategory());
        assertEquals("이미 납부된 건", record.getRejectReason());
        assertNotNull(record.getRejectedAt());
    }

    @Test
    public void 반려사유_미입력시_reject_무시() {
        PaymentRecord record = new PaymentRecord(contract, 100_000L, "카드");
        // enterRejectInfo 호출 안 함
        record.reject();

        // 상태가 REJECTED로 바뀌지 않아야 함
        assertEquals(PaymentRecordStatus.WAITING, record.getStatus());
        assertNull(record.getRejectedAt());
    }

    @Test
    public void 반려_분류별_입력() {
        PaymentRecord record = new PaymentRecord(contract, 100_000L, "카드");
        record.enterRejectInfo(RejectCategory.CONTRACT_MISMATCH, "계약 불일치 사유");

        assertEquals(RejectCategory.CONTRACT_MISMATCH, record.getRejectCategory());
    }
}
