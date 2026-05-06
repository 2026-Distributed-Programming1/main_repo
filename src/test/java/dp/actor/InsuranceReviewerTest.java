package dp.actor;

import dp.consultation.PolicyApplication;
import dp.consultation.ReviewResult;
import dp.consultation.Underwriting;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * InsuranceReviewer 단위 테스트
 *
 * 검증 대상:
 * - 생성자: Employee 상속 속성 정상 설정
 * - startUnderwriting(): Underwriting 객체 생성 및 심사 시작
 * - deliverReviewResult(): 심사 결과 전달
 */
public class InsuranceReviewerTest {

    @Test
    public void 생성_시_이름이_정상_설정된다() {
        InsuranceReviewer reviewer = new InsuranceReviewer("박심사", "심사팀", "심사자");
        assertEquals("박심사", reviewer.getName());
    }

    @Test
    public void startUnderwriting이_Underwriting을_반환한다() {
        InsuranceReviewer reviewer = new InsuranceReviewer("박심사", "심사팀", "심사자");
        PolicyApplication application = new PolicyApplication();
        application.enterCustomerInfo("홍길동", "2000-01-01", "010-1234-5678", "서울");
        application.selectProduct("실손의료보험", 20, "월납");

        Underwriting underwriting = reviewer.startUnderwriting(application);

        assertNotNull(underwriting);
        assertNotNull(underwriting.getReviewedAt());
    }

    @Test
    public void deliverReviewResult가_정상_동작한다() {
        InsuranceReviewer reviewer = new InsuranceReviewer("박심사", "심사팀", "심사자");
        ReviewResult result = new ReviewResult("승인", null, null);
        assertDoesNotThrow(() -> reviewer.deliverReviewResult(result));
    }

    @Test
    public void Employee를_상속한다() {
        InsuranceReviewer reviewer = new InsuranceReviewer("박심사", "심사팀", "심사자");
        assertInstanceOf(Employee.class, reviewer);
    }
}