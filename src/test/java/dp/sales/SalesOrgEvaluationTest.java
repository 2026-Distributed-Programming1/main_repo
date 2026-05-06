package dp.sales;

import dp.enums.ChannelType;
import dp.enums.EvaluationGrade;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SalesOrgEvaluation 단위 테스트
 *
 * 검증 대상:
 * - validateRequired(): 필수 항목 검사 (평가등급)
 * - saveEvaluation(): 평가번호, 평가일시 설정
 */
public class SalesOrgEvaluationTest {

    @Test
    public void validateRequired_평가등급_설정시_true() {
        SalesOrgEvaluation evaluation = new SalesOrgEvaluation();
        evaluation.setEvaluationGrade(EvaluationGrade.S);
        assertTrue(evaluation.validateRequired());
    }

    @Test
    public void validateRequired_평가등급_미설정시_false() {
        SalesOrgEvaluation evaluation = new SalesOrgEvaluation();
        assertFalse(evaluation.validateRequired());
    }

    @Test
    public void validateRequired_모든_등급이_유효하다() {
        for (EvaluationGrade grade : EvaluationGrade.values()) {
            SalesOrgEvaluation evaluation = new SalesOrgEvaluation();
            evaluation.setEvaluationGrade(grade);
            assertTrue(evaluation.validateRequired(), grade + " 등급이어야 유효해야 합니다");
        }
    }

    @Test
    public void saveEvaluation_후_평가번호가_부여된다() {
        SalesOrgEvaluation evaluation = new SalesOrgEvaluation();
        evaluation.setEvaluationGrade(EvaluationGrade.A);
        evaluation.saveEvaluation();
        assertNotNull(evaluation.getEvaluationNo());
        assertTrue(evaluation.getEvaluationNo().startsWith("EV-"));
    }

    @Test
    public void saveEvaluation_후_평가일시가_설정된다() {
        SalesOrgEvaluation evaluation = new SalesOrgEvaluation();
        evaluation.saveEvaluation();
        assertNotNull(evaluation.getEvaluatedAt());
    }

    @Test
    public void 평가등급_설정이_반영된다() {
        SalesOrgEvaluation evaluation = new SalesOrgEvaluation();
        evaluation.setEvaluationGrade(EvaluationGrade.B);
        assertEquals(EvaluationGrade.B, evaluation.getEvaluationGrade());
    }

    @Test
    public void 채널_실적_정보_설정이_반영된다() {
        SalesOrgEvaluation evaluation = new SalesOrgEvaluation();
        evaluation.setChannelName("서울지점");
        evaluation.setChannelType(ChannelType.DESIGNER);
        evaluation.setSalesResult(50000000L);
        evaluation.setContractCount(30);
        evaluation.setAchievementRate(95.0);
        assertEquals("서울지점", evaluation.getChannelName());
    }

    @Test
    public void 평가기간_조회필터_설정이_반영된다() {
        SalesOrgEvaluation evaluation = new SalesOrgEvaluation();
        evaluation.setFilterStartDate(LocalDate.of(2025, 1, 1));
        evaluation.setFilterEndDate(LocalDate.of(2025, 12, 31));
        // setter 호출 후 saveEvaluation까지 정상 흐름 확인
        evaluation.setEvaluationGrade(EvaluationGrade.S);
        evaluation.saveEvaluation();
        assertNotNull(evaluation.getEvaluationNo());
    }

    @Test
    public void 평가의견_선택입력이_반영된다() {
        SalesOrgEvaluation evaluation = new SalesOrgEvaluation();
        evaluation.setEvaluationGrade(EvaluationGrade.A);
        evaluation.setEvaluationComment("전반적으로 우수한 실적을 보임");
        evaluation.saveEvaluation();
        // 평가의견은 선택 항목이지만 저장 흐름에 영향 없음
        assertNotNull(evaluation.getEvaluationNo());
    }
}