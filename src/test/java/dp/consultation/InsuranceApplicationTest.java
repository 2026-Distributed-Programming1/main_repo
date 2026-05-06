package dp.consultation;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * InsuranceApplication 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 신청번호 자동 부여, 특약목록 초기화
 * - enterPersonalInfo(): 개인정보 입력
 * - selectSpecialTerms(): 특약 선택 (A1)
 * - selectPaymentMethod(): 납입방법 선택
 * - authenticate(): 본인인증
 * - apply(): 신청 및 신청일시 설정
 */
public class InsuranceApplicationTest {

    @Test
    public void 생성_시_신청번호가_부여되고_특약목록이_초기화된다() {
        InsuranceApplication application = new InsuranceApplication();

        assertTrue(application.getApplicationNumber() > 0);
        assertNotNull(application.getSelectedSpecialTerms());
        assertTrue(application.getSelectedSpecialTerms().isEmpty());
    }

    @Test
    public void 개인정보_입력이_정상_동작한다() {
        InsuranceApplication application = new InsuranceApplication();
        assertDoesNotThrow(() ->
                application.enterPersonalInfo("홍길동", "2000-01-01", "010-1234-5678", "서울시 강남구"));
    }

    @Test
    public void A1_특약선택이_정상_반영된다() {
        InsuranceApplication application = new InsuranceApplication();
        List<String> terms = Arrays.asList("암 특약", "치아 특약");
        application.selectSpecialTerms(terms);

        assertEquals(2, application.getSelectedSpecialTerms().size());
        assertTrue(application.getSelectedSpecialTerms().contains("암 특약"));
        assertTrue(application.getSelectedSpecialTerms().contains("치아 특약"));
    }

    @Test
    public void 납입방법_선택이_정상_반영된다() {
        InsuranceApplication application = new InsuranceApplication();
        application.selectPaymentMethod("월납");
        assertEquals("월납", application.getPaymentMethod());
    }

    @Test
    public void 본인인증이_정상_동작한다() {
        InsuranceApplication application = new InsuranceApplication();
        assertTrue(application.authenticate());
    }

    @Test
    public void apply_후_신청일시가_설정된다() {
        InsuranceApplication application = new InsuranceApplication();
        application.enterPersonalInfo("홍길동", "2000-01-01", "010-1234-5678", "서울시");
        application.selectPaymentMethod("월납");
        application.authenticate();
        application.apply();

        assertNotNull(application.getAppliedAt());
    }

    @Test
    public void 특약없이_신청_가능하다() {
        InsuranceApplication application = new InsuranceApplication();
        application.selectSpecialTerms(List.of());
        assertTrue(application.getSelectedSpecialTerms().isEmpty());
    }
}