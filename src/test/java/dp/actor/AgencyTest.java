package dp.actor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Agency 단위 테스트
 *
 * 검증 대상:
 * - 생성자: SalesChannel 상속 속성 + 대리점번호 정상 설정
 * - getAgencyNumber(): 대리점번호 반환
 * - setAgencyNumber(): 대리점번호 변경
 */
public class AgencyTest {

    @Test
    public void 생성_시_속성이_정상_설정된다() {
        Agency agency = new Agency(2, "한국대리점", "부산", "A-2024-001");

        assertEquals("한국대리점", agency.getName());
        assertEquals("A-2024-001", agency.getAgencyNumber());
    }

    @Test
    public void 대리점번호_변경이_정상_반영된다() {
        Agency agency = new Agency(2, "한국대리점", "부산", "A-2024-001");
        agency.setAgencyNumber("A-2025-999");
        assertEquals("A-2025-999", agency.getAgencyNumber());
    }

    @Test
    public void SalesChannel을_상속한다() {
        Agency agency = new Agency(2, "한국대리점", "부산", "A-2024-001");
        assertInstanceOf(SalesChannel.class, agency);
    }
}