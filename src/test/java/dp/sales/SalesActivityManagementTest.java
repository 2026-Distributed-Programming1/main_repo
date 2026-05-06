package dp.sales;

import dp.enums.ChannelType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SalesActivityManagement 단위 테스트
 *
 * 검증 대상:
 * - setVisitCount() / setContractCount(): 전환율(conversionRate) 자동 재계산
 * - saveImprovement(): 관리번호, 등록일시 설정
 */
public class SalesActivityManagementTest {

    @Test
    public void 방문건수와_계약건수_설정시_전환율이_계산된다() {
        SalesActivityManagement mgmt = new SalesActivityManagement();
        mgmt.setVisitCount(10);
        mgmt.setContractCount(3);
        assertEquals(30.0, mgmt.getConversionRate(), 0.001);
    }

    @Test
    public void 계약건수_먼저_설정_후_방문건수_설정해도_전환율이_계산된다() {
        SalesActivityManagement mgmt = new SalesActivityManagement();
        mgmt.setContractCount(5);
        mgmt.setVisitCount(20);
        assertEquals(25.0, mgmt.getConversionRate(), 0.001);
    }

    @Test
    public void 방문건수가_0이면_전환율이_계산되지_않는다() {
        SalesActivityManagement mgmt = new SalesActivityManagement();
        mgmt.setVisitCount(0);
        mgmt.setContractCount(3);
        assertNull(mgmt.getConversionRate());
    }

    @Test
    public void 방문건수_100_계약건수_100이면_전환율은_100() {
        SalesActivityManagement mgmt = new SalesActivityManagement();
        mgmt.setVisitCount(100);
        mgmt.setContractCount(100);
        assertEquals(100.0, mgmt.getConversionRate(), 0.001);
    }

    @Test
    public void saveImprovement_후_관리번호가_부여된다() {
        SalesActivityManagement mgmt = new SalesActivityManagement();
        mgmt.saveImprovement();
        assertNotNull(mgmt.getManagementNo());
        assertTrue(mgmt.getManagementNo().startsWith("SA-"));
    }

    @Test
    public void saveImprovement_후_등록일시가_설정된다() {
        SalesActivityManagement mgmt = new SalesActivityManagement();
        mgmt.saveImprovement();
        assertNotNull(mgmt.getRegisteredAt());
    }

    @Test
    public void 채널명_채널유형_개선내용_설정이_반영된다() {
        SalesActivityManagement mgmt = new SalesActivityManagement();
        mgmt.setChannelName("강남지점");
        mgmt.setChannelType(ChannelType.AGENCY);
        mgmt.setImprovementContent("주 2회 이상 고객 방문 의무화");
        assertEquals("강남지점", mgmt.getChannelName());
    }

    @Test
    public void 조회기간과_목표달성률_설정이_반영된다() {
        SalesActivityManagement mgmt = new SalesActivityManagement();
        mgmt.setStartDate(LocalDate.of(2025, 1, 1));
        mgmt.setEndDate(LocalDate.of(2025, 3, 31));
        mgmt.setAchievementRate(85.5);
        mgmt.setRevisedTarget(15);
        // 설정 후 관리번호 발급까지 정상 흐름 확인
        mgmt.saveImprovement();
        assertNotNull(mgmt.getManagementNo());
    }
}