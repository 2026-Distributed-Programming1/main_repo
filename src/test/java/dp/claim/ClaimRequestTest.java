package dp.claim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dp.actor.Customer;
import dp.common.BankAccount;
import dp.contract.Contract;
import dp.enums.AccidentSubType;
import dp.enums.AuthMethod;
import dp.enums.ClaimRequestStatus;
import dp.enums.ClaimType;
import dp.enums.NoticeMethod;
import java.time.LocalDate;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * ClaimRequest 단위 테스트
 * <p>
 * 검증 대상: - 생성자, 본인인증, 청구 유형 선택 (A2: 재해) - 등록 계좌 선택 (A4) - 새 계좌 등록 후 인증 (E1: 미인증 계좌) - 최종 검증 및 제출
 */
public class ClaimRequestTest {

  private Customer customer;
  private Contract contract;

  @BeforeEach
  public void setUp() {
    customer = new Customer("청구고객", "900101-1234567", "010-0000-0000", "test@test.com");
    contract = new Contract(customer, LocalDate.now().minusYears(1),
        LocalDate.now().plusYears(9), 500_000L);
  }

  @Test
  public void 생성_시_청구번호와_초기상태가_설정된다() {
    ClaimRequest claim = new ClaimRequest(customer, contract);

    assertNotNull(claim.getClaimNo());
    assertTrue(claim.getClaimNo().startsWith("CLM"));
    assertEquals(ClaimRequestStatus.DRAFT, claim.getStatus());
    assertEquals(customer, claim.getCustomer());
    assertEquals(contract, claim.getContract());
  }

  @Test
  public void 본인인증_정상_처리() {
    ClaimRequest claim = new ClaimRequest(customer, contract);
    claim.selectAuthMethod(AuthMethod.MOBILE);
    boolean result = claim.authenticate();

    assertTrue(result);
    assertTrue(claim.isAuthenticated());
  }

  @Test
  public void 인증방법_미선택시_authenticate_실패() {
    ClaimRequest claim = new ClaimRequest(customer, contract);
    // selectAuthMethod 호출 안 함
    boolean result = claim.authenticate();

    assertFalse(result);
  }

  @Test
  public void 수령인_정보_확인시_고객_정보로_자동_입력() {
    ClaimRequest claim = new ClaimRequest(customer, contract);
    claim.confirmRecipientInfo();

    assertNotNull(claim.getRecipientInfo());
    assertEquals(customer.getName(), claim.getRecipientInfo().getName());
  }

  @Test
  public void 수령인_연락처_변경() {
    ClaimRequest claim = new ClaimRequest(customer, contract);
    claim.confirmRecipientInfo();
    claim.changeRecipientContact("010-9999-9999");

    assertEquals("010-9999-9999", claim.getRecipientInfo().getContact());
  }

  @Test
  public void A2_재해_사고상세_입력() {
    ClaimRequest claim = new ClaimRequest(customer, contract);
    claim.selectClaimType(ClaimType.ACCIDENT);
    AccidentDetail detail = new AccidentDetail();
    detail.enter(AccidentSubType.TRAFFIC, "후방 추돌", LocalDate.of(2025, 4, 15), "강남대로");
    claim.enterAccidentDetail(detail);

    assertEquals(ClaimType.ACCIDENT, claim.getClaimType());
    assertNotNull(claim.getAccidentDetail());
    assertEquals(AccidentSubType.TRAFFIC, claim.getAccidentDetail().getAccidentSubType());
  }

  @Test
  public void 청구사유_다중_선택() {
    ClaimRequest claim = new ClaimRequest(customer, contract);
    claim.selectClaimReasons(Arrays.asList("입원", "수술"));

    assertEquals(2, claim.getClaimReasons().size());
    assertTrue(claim.getClaimReasons().contains("입원"));
    assertTrue(claim.getClaimReasons().contains("수술"));
  }

  @Test
  public void A4_등록된_계좌_선택() {
    BankAccount account = new BankAccount();
    account.enter("국민은행", "123-456", "청구고객");
    account.verify();

    ClaimRequest claim = new ClaimRequest(customer, contract);
    claim.selectExistingAccount(account);

    assertEquals(account, claim.getBankAccount());
    assertTrue(claim.verifyAccount());
  }

  @Test
  public void 새_계좌_등록_후_인증_성공() {
    ClaimRequest claim = new ClaimRequest(customer, contract);
    claim.registerNewAccount("우리은행", "111-222-333");
    boolean result = claim.verifyAccount();

    assertTrue(result);
    assertNotNull(claim.getBankAccount());
    assertEquals("우리은행", claim.getBankAccount().getBankName());
  }

  @Test
  public void E1_계좌_미선택시_verifyAccount_실패() {
    ClaimRequest claim = new ClaimRequest(customer, contract);
    // 계좌 등록 안 함
    boolean result = claim.verifyAccount();

    assertFalse(result);
  }

  @Test
  public void 정상_입력_후_제출_시_상태_RECEIVED() {
    ClaimRequest claim = createCompleteClaim();
    claim.submit();

    assertEquals(ClaimRequestStatus.RECEIVED, claim.getStatus());
    assertNotNull(claim.getRequestedAt());
  }

  @Test
  public void 검증_실패_시_제출_안됨() {
    ClaimRequest claim = new ClaimRequest(customer, contract);
    // 아무것도 입력 안 함
    claim.submit();

    assertEquals(ClaimRequestStatus.DRAFT, claim.getStatus());
    assertNull(claim.getRequestedAt());
  }

  private ClaimRequest createCompleteClaim() {
    ClaimRequest claim = new ClaimRequest(customer, contract);
    claim.agreePersonalInfoTerms();
    claim.selectAuthMethod(AuthMethod.MOBILE);
    claim.authenticate();
    claim.selectInsured(customer);
    claim.selectClaimType(ClaimType.DISEASE);
    claim.selectClaimReasons(Arrays.asList("입원"));
    claim.enterDiagnosis("감기");
    claim.registerNewAccount("국민은행", "123-456");
    claim.verifyAccount();
    claim.selectNoticeMethod(NoticeMethod.KAKAO);
    return claim;
  }
}
