package dp.actor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Designer 단위 테스트
 *
 * 검증 대상:
 * - 생성자: SalesChannel 상속 속성 + 자격번호 정상 설정
 * - getLicenseNumber(): 자격번호 반환
 * - setLicenseNumber(): 자격번호 변경
 */
public class DesignerTest {

    @Test
    public void 생성_시_속성이_정상_설정된다() {
        Designer designer = new Designer(1, "최설계", "서울", "L-2024-001");

        assertEquals("최설계", designer.getName());
        assertEquals("L-2024-001", designer.getLicenseNumber());
    }

    @Test
    public void 자격번호_변경이_정상_반영된다() {
        Designer designer = new Designer(1, "최설계", "서울", "L-2024-001");
        designer.setLicenseNumber("L-2025-999");
        assertEquals("L-2025-999", designer.getLicenseNumber());
    }

    @Test
    public void SalesChannel을_상속한다() {
        Designer designer = new Designer(1, "최설계", "서울", "L-2024-001");
        assertInstanceOf(SalesChannel.class, designer);
    }
}