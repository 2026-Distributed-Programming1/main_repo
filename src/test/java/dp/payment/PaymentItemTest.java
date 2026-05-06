package dp.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dp.actor.Customer;
import dp.contract.Contract;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * PaymentItem 단위 테스트 (N:M 매핑 클래스)
 * <p>
 * 검증 대상: - 생성 시 회당 보험료 자동 로드 - 납입 횟수 설정 시 소계 자동 산출 - calculateSubtotal 결과 정확성
 */
public class PaymentItemTest {

  private Customer customer;
  private Contract contract;
  private Payment payment;

  @BeforeEach
  public void setUp() {
    customer = new Customer("테스트", "900101-1234567", "010-0000-0000", "test@test.com");
    contract = new Contract(customer,
        LocalDate.now().minusYears(1), LocalDate.now().plusYears(9), 100_000L);
    payment = new Payment(customer);
  }

  @Test
  public void 생성_시_회당_보험료_자동_로드() {
    PaymentItem item = new PaymentItem(payment, contract);

    assertEquals(payment, item.getPayment());
    assertEquals(contract, item.getContract());
    assertEquals(100_000L, item.getPremiumPerCount());
    assertEquals(0, item.getCount());
  }

  @Test
  public void 납입횟수_설정_시_소계_자동_산출() {
    PaymentItem item = new PaymentItem(payment, contract);
    item.setCount(3);

    assertEquals(3, item.getCount());
    assertEquals(300_000L, item.getSubtotal());
  }

  @Test
  public void calculateSubtotal_정확한_금액_반환() {
    PaymentItem item = new PaymentItem(payment, contract);
    item.setCount(5);

    assertEquals(500_000L, item.calculateSubtotal());
  }

  @Test
  public void 횟수_변경_시_소계_재계산() {
    PaymentItem item = new PaymentItem(payment, contract);
    item.setCount(2);
    assertEquals(200_000L, item.getSubtotal());

    item.setCount(10);
    assertEquals(1_000_000L, item.getSubtotal());
  }
}
