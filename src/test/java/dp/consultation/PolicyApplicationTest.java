package dp.consultation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * PolicyApplication 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 청약번호 자동 부여
 * - enterCustomerInfo(): 고객정보 입력
 * - selectProduct(): 상품 및 계약조건 선택
 * - submit(): 제출 및 제출일시 설정
 */
public class PolicyApplicationTest {

    @Test
    public void 생성_시_청약번호가_부여된다() {
        PolicyApplication application = new PolicyApplication();
        assertTrue(application.getApplicationNumber() > 0);
    }

    @Test
    public void 고객정보_입력이_정상_반영된다() {
        PolicyApplication application = new PolicyApplication();
        application.enterCustomerInfo("홍길동", "2000-01-01", "010-1234-5678", "서울시 강남구");

        assertEquals("홍길동", application.getCustomerName());
    }

    @Test
    public void 상품선택이_정상_반영된다() {
        PolicyApplication application = new PolicyApplication();
        application.selectProduct("실손의료보험", 20, "월납");

        assertEquals("실손의료보험", application.getProductName());
        assertEquals(20, application.getPeriod());
        assertEquals("월납", application.getPaymentMethod());
    }

    @Test
    public void submit_후_제출일시가_설정된다() {
        PolicyApplication application = createCompleteApplication();
        application.submit();

        assertNotNull(application.getSubmittedAt());
    }

    @Test
    public void 전자서명요청_정상_동작한다() {
        PolicyApplication application = new PolicyApplication();
        // 예외 없이 동작하면 통과
        assertDoesNotThrow(() -> application.requestElectronicSignature());
    }

    @Test
    public void 서명파일첨부_정상_동작한다() {
        PolicyApplication application = new PolicyApplication();
        assertDoesNotThrow(() -> application.attachSignature("signature.png"));
    }

    private PolicyApplication createCompleteApplication() {
        PolicyApplication application = new PolicyApplication();
        application.enterCustomerInfo("홍길동", "2000-01-01", "010-1234-5678", "서울시 강남구");
        application.selectProduct("실손의료보험", 20, "월납");
        return application;
    }
}