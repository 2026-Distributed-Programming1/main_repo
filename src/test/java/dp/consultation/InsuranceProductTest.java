package dp.consultation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * InsuranceProduct 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 모든 속성 정상 설정
 * - getter: 각 필드 정상 반환
 */
public class InsuranceProductTest {

    @Test
    public void 생성_시_모든_속성이_정상_설정된다() {
        InsuranceProduct product = new InsuranceProduct(
                "실손의료보험", "건강", 50000L, "의료비 전액 보장", "치과 제외");

        assertEquals("실손의료보험", product.getProductName());
        assertEquals("건강", product.getType());
        assertEquals(50000L, product.getMonthlyPremium());
        assertEquals("의료비 전액 보장", product.getCoverage());
        assertEquals("치과 제외", product.getSpecialTerms());
    }

    @Test
    public void 생명보험_생성이_정상_동작한다() {
        InsuranceProduct product = new InsuranceProduct(
                "종신보험", "생명", 150000L, "사망 시 1억 지급", "없음");

        assertEquals("종신보험", product.getProductName());
        assertEquals("생명", product.getType());
        assertEquals(150000L, product.getMonthlyPremium());
    }

    @Test
    public void 손해보험_생성이_정상_동작한다() {
        InsuranceProduct product = new InsuranceProduct(
                "자동차보험", "손해", 80000L, "대인/대물 무제한", "음주운전 제외");

        assertEquals("자동차보험", product.getProductName());
        assertEquals("손해", product.getType());
    }
}