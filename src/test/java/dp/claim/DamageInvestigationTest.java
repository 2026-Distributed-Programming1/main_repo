package dp.claim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dp.actor.ClaimsHandler;
import dp.actor.Customer;
import dp.contract.Contract;
import dp.enums.InvestigationResult;
import dp.enums.InvestigationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * DamageInvestigation 단위 테스트
 * <p>
 * 검증 대상: - 생성자: 조사번호, 상태 NEW_ASSIGNED - 담당자 배정 시 INVESTIGATING으로 전이 - E1: 과실 비율 합 != 100% 검증 - A1: 보완 서류 요청 - A2: 추가 조사 지시 - A3: 면책 종결 - 정상 흐름: complete() → ClaimCalculation 반환
 */
public class DamageInvestigationTest {

  private ClaimRequest claim;
  private ClaimsHandler handler;

  @BeforeEach
  public void setUp() {
    Customer customer = new Customer("테스트고객", "900101-1234567", "010-0000-0000", "test@test.com");
    Contract contract = new Contract(customer,
        LocalDate.now().minusYears(1), LocalDate.now().plusYears(9), 500_000L);
    claim = new ClaimRequest(customer, contract);
    handler = new ClaimsHandler("박보상", "보상팀", "대리", 5_000_000L);
  }

  @Test
  public void 생성_시_조사번호와_초기상태_NEW_ASSIGNED() {
    DamageInvestigation inv = new DamageInvestigation(claim);

    assertNotNull(inv.getInvestigationNo());
    assertTrue(inv.getInvestigationNo().startsWith("INV"));
    assertEquals(InvestigationStatus.NEW_ASSIGNED, inv.getStatus());
    assertEquals(claim, inv.getClaim());
    assertNull(inv.getHandler());
  }

  @Test
  public void 담당자_배정_시_상태_INVESTIGATING() {
    DamageInvestigation inv = new DamageInvestigation(claim);
    inv.assignHandler(handler);

    assertEquals(handler, inv.getHandler());
    assertEquals(InvestigationStatus.INVESTIGATING, inv.getStatus());
  }

  @Test
  public void E1_과실비율_합_100_아니면_검증_실패() {
    DamageInvestigation inv = new DamageInvestigation(claim);
    inv.enterFaultRatio(60.0, 30.0); // 합 90%

    assertFalse(inv.validateFaultRatio());
  }

  @Test
  public void E1_과실비율_합_100이면_검증_통과() {
    DamageInvestigation inv = new DamageInvestigation(claim);
    inv.enterFaultRatio(70.0, 30.0);

    assertTrue(inv.validateFaultRatio());
  }

  @Test
  public void E2_필수입력_누락시_validateRequired_실패() {
    DamageInvestigation inv = new DamageInvestigation(claim);
    // 아무 입력도 하지 않음
    assertFalse(inv.validateRequired());
  }

  @Test
  public void 정상흐름_complete_시_ClaimCalculation_반환() {
    DamageInvestigation inv = createCompleteInvestigation();
    ClaimCalculation calc = inv.complete();

    assertNotNull(calc);
    assertEquals(InvestigationStatus.INVESTIGATED, inv.getStatus());
    assertNotNull(inv.getInvestigatedAt());
  }

  @Test
  public void 면책결과_complete_시_calculation_반환_안됨() {
    DamageInvestigation inv = new DamageInvestigation(claim);
    inv.assignHandler(handler);
    inv.enterRecognizedDamage(1_000_000L);
    inv.enterFaultRatio(50.0, 50.0);
    inv.enterOpinion("의견");
    inv.selectResult(InvestigationResult.REJECTED);
    inv.enterRejectReason("약관 미해당");

    ClaimCalculation calc = inv.complete();
    // 면책일 때는 산출 객체가 반환되지 않음
    assertNull(calc);
  }

  @Test
  public void A1_보완서류_요청() {
    DamageInvestigation inv = new DamageInvestigation(claim);
    inv.requestSupplement(Arrays.asList("진단서", "사고경위서"), "보완 부탁드립니다");

    assertNotNull(inv.getSupplementRequest());
    assertEquals(2, inv.getSupplementRequest().getRequestedItems().size());
    assertNotNull(inv.getSupplementRequest().getSentAt());
  }

  @Test
  public void A2_추가조사_지시() {
    DamageInvestigation inv = new DamageInvestigation(claim);
    LocalDateTime schedule = LocalDateTime.now().plusDays(3);
    inv.requestAdditionalInvestigation("정비소", schedule, "차량 손상도 확인");

    assertNotNull(inv.getAdditionalInvestigation());
    assertEquals("정비소", inv.getAdditionalInvestigation().getVisitLocation());
    assertEquals(schedule, inv.getAdditionalInvestigation().getSchedule());
  }

  @Test
  public void A3_면책_종결처리_시_상태_CLOSED() {
    DamageInvestigation inv = new DamageInvestigation(claim);
    inv.selectResult(InvestigationResult.REJECTED);
    inv.enterRejectReason("약관 미해당");
    inv.closeAsRejected();

    assertEquals(InvestigationStatus.CLOSED, inv.getStatus());
  }

  private DamageInvestigation createCompleteInvestigation() {
    DamageInvestigation inv = new DamageInvestigation(claim);
    inv.assignHandler(handler);
    inv.enterRecognizedDamage(3_000_000L);
    inv.enterFaultRatio(70.0, 30.0);
    inv.enterOpinion("후방 추돌, 우리 고객 70%");
    inv.selectResult(InvestigationResult.APPROVED);
    return inv;
  }
}
