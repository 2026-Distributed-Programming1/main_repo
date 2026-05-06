package dp.consultation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Underwriting 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 심사번호 자동 부여
 * - startReview(): 심사 시작 및 일시 설정
 * - autoReview(): 자동심사 결과 반환
 * - manualReview(): 수동심사 결과 반환 (A1)
 * - complete(): 심사 완료 처리
 */
public class UnderwritingTest {

    @Test
    public void 생성_시_심사번호가_부여된다() {
        Underwriting underwriting = new Underwriting();
        assertTrue(underwriting.getReviewNumber() > 0);
    }

    @Test
    public void 심사시작_후_일시가_설정된다() {
        Underwriting underwriting = new Underwriting();
        underwriting.startReview();
        assertNotNull(underwriting.getReviewedAt());
    }

    @Test
    public void 자동심사_결과가_반환된다() {
        Underwriting underwriting = new Underwriting();
        underwriting.startReview();
        ReviewResult result = underwriting.autoReview();

        assertNotNull(result);
        assertNotNull(underwriting.getRiskGrade());
    }

    @Test
    public void A1_수동심사_결과가_반환된다() {
        Underwriting underwriting = new Underwriting();
        underwriting.startReview();
        ReviewResult result = underwriting.manualReview("진단심사", "고혈압 이력 확인 필요");

        assertNotNull(result);
        assertEquals("진단심사", underwriting.getReviewType());
        assertEquals("고혈압 이력 확인 필요", underwriting.getReviewOpinion());
    }

    @Test
    public void 서류첨부_정상_동작한다() {
        Underwriting underwriting = new Underwriting();
        assertDoesNotThrow(() -> underwriting.attachDocument("document.pdf"));
    }

    @Test
    public void complete_승인_처리가_정상_반영된다() {
        Underwriting underwriting = new Underwriting();
        underwriting.startReview();
        underwriting.complete("승인", null, null);

        assertNotNull(underwriting.getReviewResult());
        assertEquals("승인", underwriting.getReviewResult().getResult());
    }

    @Test
    public void complete_조건부승인_처리가_정상_반영된다() {
        Underwriting underwriting = new Underwriting();
        underwriting.startReview();
        underwriting.complete("조건부승인", "고혈압 특약 제외", null);

        assertEquals("조건부승인", underwriting.getReviewResult().getResult());
        assertEquals("고혈압 특약 제외", underwriting.getReviewResult().getCondition());
    }

    @Test
    public void complete_거절_처리가_정상_반영된다() {
        Underwriting underwriting = new Underwriting();
        underwriting.startReview();
        underwriting.complete("거절", null, "기저질환으로 인수 불가");

        assertEquals("거절", underwriting.getReviewResult().getResult());
        assertEquals("기저질환으로 인수 불가", underwriting.getReviewResult().getRejectionReason());
    }
}