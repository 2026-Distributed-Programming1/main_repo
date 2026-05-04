package com.insurance.test.payment;

import com.insurance.actor.Customer;
import com.insurance.common.BankAccount;
import com.insurance.contract.InsuranceContract;
import com.insurance.enums.PaymentMethod;
import com.insurance.enums.PaymentStatus;
import com.insurance.payment.Payment;
import com.insurance.payment.PaymentItem;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Payment 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 납입 신청번호, 초기 상태 DRAFT
 * - N:M 매핑 (selectContracts → PaymentItem 자동 생성)
 * - 정상 흐름: 횟수 입력 → 계좌 인증 → 총액 계산 → 신청
 * - 선납 할인 (2회분 이상)
 * - E1: 미인증 계좌로 신청 시 ERROR 상태
 */
public class PaymentTest {

    private Customer customer;
    private InsuranceContract contract1;
    private InsuranceContract contract2;
    private BankAccount account;

    @Before
    public void setUp() {
        customer = new Customer("테스트", "900101-1234567", "010-0000-0000", "test@test.com");
        contract1 = new InsuranceContract(customer,
                LocalDate.now().minusYears(1), LocalDate.now().plusYears(9), 100_000L);
        contract2 = new InsuranceContract(customer,
                LocalDate.now().minusMonths(6), LocalDate.now().plusYears(9), 200_000L);

        account = new BankAccount();
        account.enter("국민은행", "123-456", "테스트");
        account.verify();
    }

    @Test
    public void 생성_시_납입신청번호와_초기상태_DRAFT() {
        Payment payment = new Payment(customer);

        assertNotNull(payment.getPaymentNo());
        assertTrue(payment.getPaymentNo().startsWith("PAY"));
        assertEquals(PaymentStatus.DRAFT, payment.getStatus());
        assertEquals(customer, payment.getCustomer());
        assertTrue(payment.getItems().isEmpty());
    }

    @Test
    public void selectContracts_시_PaymentItem_자동_생성() {
        Payment payment = new Payment(customer);
        payment.selectContracts(Arrays.asList(contract1, contract2));

        assertEquals(2, payment.getItems().size());
        assertEquals(contract1, payment.getItems().get(0).getContract());
        assertEquals(contract2, payment.getItems().get(1).getContract());
    }

    @Test
    public void enterPaymentCount_시_PaymentItem_횟수_반영() {
        Payment payment = new Payment(customer);
        payment.selectContracts(Collections.singletonList(contract1));
        PaymentItem item = payment.getItems().get(0);

        payment.enterPaymentCount(item, 3);

        assertEquals(3, item.getCount());
    }

    @Test
    public void 모든_횟수_입력시_validatePaymentCount_통과() {
        Payment payment = new Payment(customer);
        payment.selectContracts(Arrays.asList(contract1, contract2));
        payment.enterPaymentCount(payment.getItems().get(0), 1);
        payment.enterPaymentCount(payment.getItems().get(1), 1);

        assertTrue(payment.validatePaymentCount());
    }

    @Test
    public void 횟수_미입력시_validatePaymentCount_실패() {
        Payment payment = new Payment(customer);
        payment.selectContracts(Arrays.asList(contract1, contract2));
        // count 미입력 (기본값 0)

        assertFalse(payment.validatePaymentCount());
    }

    @Test
    public void 총액_계산_정확성() {
        // contract1: 100,000 × 1 = 100,000
        // contract2: 200,000 × 1 = 200,000
        // 총 300,000원, 1회분만 납입 → 선납 할인 없음
        Payment payment = new Payment(customer);
        payment.selectContracts(Arrays.asList(contract1, contract2));
        payment.enterPaymentCount(payment.getItems().get(0), 1);
        payment.enterPaymentCount(payment.getItems().get(1), 1);

        long total = payment.calculateTotal();

        assertEquals(300_000L, payment.getTotalAmount());
        assertEquals(0L, payment.getEarlyDiscount());
        assertEquals(300_000L, total);
    }

    @Test
    public void 선납_할인_적용_2회분_이상() {
        // contract1: 100,000 × 3 = 300,000원
        // 3회분 납입 → 1% 할인 = 3,000원
        Payment payment = new Payment(customer);
        payment.selectContracts(Collections.singletonList(contract1));
        payment.enterPaymentCount(payment.getItems().get(0), 3);

        payment.calculateTotal();

        assertEquals(300_000L, payment.getTotalAmount());
        assertEquals(3_000L, payment.getEarlyDiscount());
        assertEquals(297_000L, payment.getDiscountedAmount());
    }

    @Test
    public void 정상_제출_후_상태_COMPLETED() {
        Payment payment = createCompletePayment();
        payment.submit();

        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
        assertNotNull(payment.getRequestedAt());
    }

    @Test
    public void E1_계좌_미인증시_상태_ERROR() {
        Payment payment = new Payment(customer);
        payment.selectContracts(Collections.singletonList(contract1));
        payment.enterPaymentCount(payment.getItems().get(0), 1);
        payment.selectPaymentMethod(PaymentMethod.IMMEDIATE_TRANSFER);

        BankAccount unverified = new BankAccount();
        payment.selectExistingAccount(unverified); // verify() 호출 안 함
        payment.submit();

        assertEquals(PaymentStatus.ERROR, payment.getStatus());
    }

    @Test
    public void A2_납입방법_가상계좌_선택() {
        Payment payment = new Payment(customer);
        payment.selectPaymentMethod(PaymentMethod.VIRTUAL_ACCOUNT);

        assertEquals(PaymentMethod.VIRTUAL_ACCOUNT, payment.getPaymentMethod());
    }

    @Test
    public void A3_새_계좌_등록_및_인증() {
        Payment payment = new Payment(customer);
        payment.registerNewAccount("우리은행", "111-222-333", "테스트");

        assertNotNull(payment.getAccount());
        assertEquals("우리은행", payment.getAccount().getBankName());
        assertTrue(payment.verifyAccount());
    }

    private Payment createCompletePayment() {
        Payment payment = new Payment(customer);
        payment.selectContracts(Collections.singletonList(contract1));
        payment.enterPaymentCount(payment.getItems().get(0), 1);
        payment.selectPaymentMethod(PaymentMethod.IMMEDIATE_TRANSFER);
        payment.selectExistingAccount(account);
        return payment;
    }
}
