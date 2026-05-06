package dp.consultation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * InterviewRecord 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 기록번호 자동 부여
 * - save(): 면담 기록 저장 및 일시 설정
 * - modify(): 기록 수정 (A3)
 * - validateRequiredFields(): 필수항목 검증 (E1, E2)
 * - navigateToProposal(): 제안서로 이동
 */
public class InterviewRecordTest {

    @Test
    public void 생성_시_기록번호가_부여된다() {
        InterviewRecord record = new InterviewRecord();
        assertTrue(record.getRecordNumber() > 0);
    }

    @Test
    public void 저장_후_면담정보가_정상_반영된다() {
        InterviewRecord record = new InterviewRecord();
        record.save("종신보험 관심 있음", "긍정적", "다음 주 재방문");

        assertEquals("종신보험 관심 있음", record.getContent());
        assertEquals("긍정적", record.getCustomerReaction());
        assertEquals("다음 주 재방문", record.getFollowUpAction());
        assertNotNull(record.getInterviewedAt());
    }

    @Test
    public void A3_수정_후_정보가_변경된다() {
        InterviewRecord record = new InterviewRecord();
        record.save("종신보험 관심", "긍정적", "재방문");
        record.modify("실손보험 관심으로 변경", "매우 긍정적", "계약 예정");

        assertEquals("실손보험 관심으로 변경", record.getContent());
        assertEquals("매우 긍정적", record.getCustomerReaction());
        assertEquals("계약 예정", record.getFollowUpAction());
    }

    @Test
    public void 필수항목_입력시_검증_통과() {
        InterviewRecord record = new InterviewRecord();
        record.save("면담 내용", "긍정적", "");
        assertTrue(record.validateRequiredFields());
    }

    @Test
    public void E1_면담내용_미입력시_검증_실패() {
        InterviewRecord record = new InterviewRecord();
        record.save("", "긍정적", "");
        assertFalse(record.validateRequiredFields());
    }

    @Test
    public void 고객명_설정이_정상_반영된다() {
        InterviewRecord record = new InterviewRecord();
        record.setCustomerName("홍길동");
        assertEquals("홍길동", record.getCustomerName());
    }

    @Test
    public void navigateToProposal_이_Proposal_반환한다() {
        InterviewRecord record = new InterviewRecord();
        Proposal proposal = record.navigateToProposal();
        assertNotNull(proposal);
    }
}