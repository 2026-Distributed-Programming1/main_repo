package dp.consultation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ReviewResult 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 결과/조건/거절사유 정상 설정
 * - deliver(): 심사 결과 전달
 * - confirm(): 확인 완료 및 확인일시 설정
 */
public class ReviewResultTest {

    @Test
    public void 승인_결과_생성이_정상_반영된다() {
        ReviewResult result = new ReviewResult("승인", null, null);

        assertEquals("승인", result.getResult());
        assertNull(result.getCondition());
        assertNull(result.getRejectionReason());
    }

    @Test
    public void 조건부승인_결과_생성이_정상_반영된다() {
        ReviewResult result = new ReviewResult("조건부승인", "고혈압 특약 제외", null);

        assertEquals("조건부승인", result.getResult());
        assertEquals("고혈압 특약 제외", result.getCondition());
        assertNull(result.getRejectionReason());
    }

    @Test
    public void 거절_결과_생성이_정상_반영된다() {
        ReviewResult result = new ReviewResult("거절", null, "기저질환으로 인수 불가");

        assertEquals("거절", result.getResult());
        assertNull(result.getCondition());
        assertEquals("기저질환으로 인수 불가", result.getRejectionReason());
    }

    @Test
    public void deliver_정상_동작한다() {
        ReviewResult result = new ReviewResult("승인", null, null);
        assertDoesNotThrow(() -> result.deliver());
    }

    @Test
    public void confirm_후_확인일시가_설정된다() {
        ReviewResult result = new ReviewResult("승인", null, null);
        result.confirm();
        assertNotNull(result.getConfirmedAt());
    }

    @Test
    public void setResult로_결과_변경이_가능하다() {
        ReviewResult result = new ReviewResult("승인", null, null);
        result.setResult("거절");
        assertEquals("거절", result.getResult());
    }

    @Test
    public void setCondition으로_조건_설정이_가능하다() {
        ReviewResult result = new ReviewResult("조건부승인", null, null);
        result.setCondition("고혈압 특약 제외");
        assertEquals("고혈압 특약 제외", result.getCondition());
    }
}