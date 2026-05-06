package dp.sales;

import dp.enums.ChannelType;
import dp.enums.EvaluationGrade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BonusRequest 단위 테스트
 *
 * 검증 대상:
 * - setEvaluationGrade(): 등급 설정 시 bonusRatio 자동 계산 (S=1.5, A=1.2)
 * - calculateBonus(): 성과급 금액 산출
 * - submit(): 요청번호, 요청일시 설정
 */
public class BonusRequestTest {

    @Test
    public void S등급_설정시_bonusRatio가_1_5이다() {
        BonusRequest request = new BonusRequest();
        request.setEvaluationGrade(EvaluationGrade.S);
        assertEquals(1.5, request.getBonusRatio());
    }

    @Test
    public void A등급_설정시_bonusRatio가_1_2이다() {
        BonusRequest request = new BonusRequest();
        request.setEvaluationGrade(EvaluationGrade.A);
        assertEquals(1.2, request.getBonusRatio());
    }

    @Test
    public void calculateBonus_S등급_기본급_기반_정상_계산() {
        BonusRequest request = new BonusRequest();
        request.setBaseSalary(3000000L);
        request.setEvaluationGrade(EvaluationGrade.S);
        Long bonus = request.calculateBonus();
        assertEquals(4500000L, bonus);
    }

    @Test
    public void calculateBonus_A등급_기본급_기반_정상_계산() {
        BonusRequest request = new BonusRequest();
        request.setBaseSalary(3000000L);
        request.setEvaluationGrade(EvaluationGrade.A);
        Long bonus = request.calculateBonus();
        assertEquals(3600000L, bonus);
    }

    @Test
    public void calculateBonus_기본급_null이면_0반환() {
        BonusRequest request = new BonusRequest();
        request.setEvaluationGrade(EvaluationGrade.S);
        assertEquals(0L, request.calculateBonus());
    }

    @Test
    public void calculateBonus_bonusRatio_null이면_0반환() {
        BonusRequest request = new BonusRequest();
        request.setBaseSalary(3000000L);
        assertEquals(0L, request.calculateBonus());
    }

    @Test
    public void calculateBonus_후_bonusAmount가_저장된다() {
        BonusRequest request = new BonusRequest();
        request.setBaseSalary(2000000L);
        request.setEvaluationGrade(EvaluationGrade.A);
        request.calculateBonus();
        assertEquals(2400000L, request.getBonusAmount());
    }

    @Test
    public void submit_후_requestNo가_부여된다() {
        BonusRequest request = new BonusRequest();
        request.submit();
        assertNotNull(request.getRequestNo());
        assertTrue(request.getRequestNo().startsWith("BR-"));
    }

    @Test
    public void submit_후_requestedAt이_설정된다() {
        BonusRequest request = new BonusRequest();
        request.submit();
        assertNotNull(request.getRequestedAt());
    }

    @Test
    public void 채널명_및_채널유형_설정이_반영된다() {
        BonusRequest request = new BonusRequest();
        request.setChannelName("강남지점");
        request.setChannelType(ChannelType.AGENCY);
        assertEquals("강남지점", request.getChannelName());
        assertEquals(ChannelType.AGENCY, request.getChannelType());
    }
}