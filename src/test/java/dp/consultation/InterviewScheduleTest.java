package dp.consultation;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * InterviewSchedule 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 면담번호 자동 부여
 * - register(): 면담 등록 및 상태 "예정"
 * - modify(): 면담 정보 수정 (A4)
 * - cancel(): 상태 "취소" (A5)
 * - validateRequiredFields(): 필수항목 검증 (E1, E2)
 */
public class InterviewScheduleTest {

    @Test
    public void 생성_시_면담번호가_부여된다() {
        InterviewSchedule schedule = new InterviewSchedule();
        assertTrue(schedule.getInterviewNumber() > 0);
    }

    @Test
    public void 면담등록_후_정보가_정상_반영된다() {
        InterviewSchedule schedule = new InterviewSchedule();
        LocalDateTime scheduled = LocalDateTime.of(2026, 5, 10, 14, 0);
        schedule.register("홍길동", scheduled, "명지대 카페", "보험 서류 지참");

        assertEquals("홍길동", schedule.getCustomerName());
        assertEquals(scheduled, schedule.getScheduledAt());
        assertEquals("명지대 카페", schedule.getLocation());
        assertEquals("보험 서류 지참", schedule.getPreparation());
        assertEquals("예정", schedule.getStatus());
    }

    @Test
    public void A4_수정_후_정보가_변경된다() {
        InterviewSchedule schedule = new InterviewSchedule();
        LocalDateTime original = LocalDateTime.of(2026, 5, 10, 14, 0);
        schedule.register("홍길동", original, "명지대 카페", "");

        LocalDateTime modified = LocalDateTime.of(2026, 5, 12, 15, 0);
        schedule.modify(modified, "강남 카페", "신분증 지참");

        assertEquals(modified, schedule.getScheduledAt());
        assertEquals("강남 카페", schedule.getLocation());
        assertEquals("신분증 지참", schedule.getPreparation());
    }

    @Test
    public void A5_취소_후_상태가_변경된다() {
        InterviewSchedule schedule = new InterviewSchedule();
        schedule.register("홍길동", LocalDateTime.now(), "카페", "");
        schedule.cancel();

        assertEquals("취소", schedule.getStatus());
    }

    @Test
    public void 유형_설정이_정상_반영된다() {
        InterviewSchedule schedule = new InterviewSchedule();
        schedule.setType("방문");
        assertEquals("방문", schedule.getType());
    }

    @Test
    public void 면담기록목록이_초기화된다() {
        InterviewSchedule schedule = new InterviewSchedule();
        assertNotNull(schedule.getInterviewRecordList());
        assertTrue(schedule.getInterviewRecordList().isEmpty());
    }
}