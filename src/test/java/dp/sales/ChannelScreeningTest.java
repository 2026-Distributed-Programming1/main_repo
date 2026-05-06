package dp.sales;

import dp.enums.ChannelType;
import dp.enums.ScreeningStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ChannelScreening 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 초기 상태(PENDING), 지원일 자동 설정, certifications 초기화
 * - addCertification(): 자격증 추가
 * - approve(): 승인번호, 승인일시 설정, 상태 변경
 * - reject(): 상태 변경
 */
public class ChannelScreeningTest {

    @Test
    public void 생성_시_초기상태는_PENDING이다() {
        ChannelScreening screening = new ChannelScreening();
        assertEquals(ScreeningStatus.PENDING, screening.getScreeningStatus());
    }

    @Test
    public void 생성_시_지원일이_오늘로_설정된다() {
        ChannelScreening screening = new ChannelScreening();
        assertEquals(LocalDate.now(), screening.getApplicationDate());
    }

    @Test
    public void 생성_시_자격증_목록이_초기화된다() {
        ChannelScreening screening = new ChannelScreening();
        assertNotNull(screening.getCertifications());
        assertTrue(screening.getCertifications().isEmpty());
    }

    @Test
    public void addCertification_자격증이_추가된다() {
        ChannelScreening screening = new ChannelScreening();
        screening.addCertification("생명보험설계사");
        screening.addCertification("손해보험설계사");
        assertEquals(2, screening.getCertifications().size());
        assertTrue(screening.getCertifications().contains("생명보험설계사"));
    }

    @Test
    public void approve_후_상태가_APPROVED로_변경된다() {
        ChannelScreening screening = new ChannelScreening();
        screening.approve();
        assertEquals(ScreeningStatus.APPROVED, screening.getScreeningStatus());
    }

    @Test
    public void approve_후_승인번호가_부여된다() {
        ChannelScreening screening = new ChannelScreening();
        screening.approve();
        assertNotNull(screening.getApprovalNo());
        assertTrue(screening.getApprovalNo().startsWith("AP-"));
    }

    @Test
    public void approve_후_승인일시가_설정된다() {
        ChannelScreening screening = new ChannelScreening();
        screening.approve();
        assertNotNull(screening.getApprovedAt());
    }

    @Test
    public void reject_후_상태가_REJECTED로_변경된다() {
        ChannelScreening screening = new ChannelScreening();
        screening.setRejectionReason("경력 기준 미달");
        screening.reject();
        assertEquals(ScreeningStatus.REJECTED, screening.getScreeningStatus());
    }

    @Test
    public void reject_후_승인번호는_null이다() {
        ChannelScreening screening = new ChannelScreening();
        screening.reject();
        assertNull(screening.getApprovalNo());
    }

    @Test
    public void 지원자_정보_설정이_반영된다() {
        ChannelScreening screening = new ChannelScreening();
        screening.setApplicantName("김영업");
        screening.setChannelType(ChannelType.DESIGNER);
        screening.setCareer("보험설계사 5년");
        assertEquals("김영업", screening.getApplicantName());
        assertEquals(ChannelType.DESIGNER, screening.getChannelType());
        assertEquals("보험설계사 5년", screening.getCareer());
    }

    @Test
    public void 조회기간_설정이_반영된다() {
        ChannelScreening screening = new ChannelScreening();
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 6, 30);
        screening.setFilterStartDate(start);
        screening.setFilterEndDate(end);
        assertEquals(start, screening.getFilterStartDate());
        assertEquals(end, screening.getFilterEndDate());
    }
}