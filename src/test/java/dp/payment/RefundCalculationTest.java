package com.insurance.test.payment;

import com.insurance.actor.Customer;
import com.insurance.contract.Cancellation;
import com.insurance.contract.InsuranceContract;
import com.insurance.enums.RefundStatus;
import com.insurance.payment.DeductionAdjustment;
import com.insurance.payment.RefundCalculation;
import com.insurance.payment.RefundPayment;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

/**
 * RefundCalculation 단위 테스트
 *
 * 검증 대상:
 * - 생성 시 자동 산출 (status=CALCULATED)
 * - 기본 환급금 = 책임준비금 × (1 + 이율)
 * - A1: 공제 항목 수기 조정 + 재산출
 * - A2: PDF 다운로드
 * - 확정 시 RefundPayment 반환
 */
public class RefundCalculationTest {

    private Cancellation cancellation;

    @Before
    public void setUp() {
        Customer customer = new Customer("테스트", "900101-1234567", "010-0000-0000", "test@test.com");
        InsuranceContract contract = new InsuranceContract(customer,
                LocalDate.now().minusYears(2), LocalDate.now().plusYears(8), 100_000L);
        cancellation = new Cancellation(contract);
        cancellation.calculateExpectedRefund();
        cancellation.confirm();
    }

    @Test
    public void 생성_시_자동_산출_및_상태_CALCULATED() {
        RefundCalculation refund = new RefundCalculation(cancellation);

        assertNotNull(refund.getRefundNo());
        assertTrue(refund.getRefundNo().startsWith("RFC"));
        assertEquals(RefundStatus.CALCULATED, refund.getStatus());
        assertNotNull(refund.getCalculatedAt());
        assertTrue(refund.getFinalRefund() > 0);
    }

    @Test
    public void 자동_로드된_데이터_검증() {
        // 월 보험료 100,000 × 24개월 = 2,400,000원
        // 책임준비금 = 2,400,000 × 0.7 = 1,680,000원
        RefundCalculation refund = new RefundCalculation(cancellation);

        assertEquals(2_400_000L, refund.getTotalPaidPremium());
        assertEquals(1_680_000L, refund.getReserveAmount());
        assertEquals("24개월", refund.getPaymentPeriod());
        assertEquals(0.025, refund.getAppliedRate(), 0.0001);
    }

    @Test
    public void 기본_환급금_계산_정확성() {
        // 책임준비금 1,680,000 × (1 + 0.025) = 1,722,000원 (반올림 오차 있음)
        RefundCalculation refund = new RefundCalculation(cancellation);

        long expected = (long) (1_680_000L * 1.025);
        assertEquals(expected, refund.getBaseRefund());
    }

    @Test
    public void 공제없는_경우_실지급금_기본환급금과_동일() {
        RefundCalculation refund = new RefundCalculation(cancellation);

        assertEquals(refund.getBaseRefund(), refund.getFinalRefund());
    }

    @Test
    public void A1_공제_조정_시_조정내역_추가() {
        RefundCalculation refund = new RefundCalculation(cancellation);
        long beforeRefund = refund.getFinalRefund();

        DeductionAdjustment adj = refund.adjustDeduction("미납 보험료", 100_000L, "조정 사유");
        refund.recalculate();

        assertNotNull(adj);
        List<DeductionAdjustment> adjustments = refund.getAdjustments();
        assertEquals(1, adjustments.size());
        assertEquals("미납 보험료", adjustments.get(0).getItemName());
        // 조정 후 환급금이 줄어들어야 함
        assertEquals(beforeRefund - 100_000L, refund.getFinalRefund());
    }

    @Test
    public void A2_PDF_다운로드_파일_반환() {
        RefundCalculation refund = new RefundCalculation(cancellation);
        File pdf = refund.exportPDF();

        assertNotNull(pdf);
    }

    @Test
    public void confirm_시_RefundPayment_반환_및_상태_PAID() {
        RefundCalculation refund = new RefundCalculation(cancellation);
        RefundPayment payment = refund.confirm();

        assertNotNull(payment);
        assertEquals(RefundStatus.PAID, refund.getStatus());
        assertNotNull(refund.getConfirmedAt());
    }

    @Test
    public void E1_해지건이_null이면_상태_PENDING_유지() {
        RefundCalculation refund = new RefundCalculation(null);

        // 자동 산출이 진행되지 않아야 함
        assertEquals(RefundStatus.CALCULATION_PENDING, refund.getStatus());
        assertEquals(0L, refund.getFinalRefund());
    }
}
