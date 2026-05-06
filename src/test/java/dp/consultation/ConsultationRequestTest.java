package dp.consultation;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ConsultationRequest 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 상담번호 자동 부여, 초기 상태 "접수"
 * - selectType(): 상담 유형 설정
 * - enterConsultationInfo(): 정보 입력
 * - validateRequiredFields(): 필수항목 검증 (E1)
 * - submit(): 상태 "접수"
 * - accept(): 상태 "수락" (판매채널 수락)
 */
public class ConsultationRequestTest {

    @Test
    public void 생성_시_상담번호가_부여된다() {
        ConsultationRequest request = new ConsultationRequest();
        assertTrue(request.getConsultationNumber() > 0);
    }

    @Test
    public void 상담유형_설정이_정상_반영된다() {
        ConsultationRequest request = new ConsultationRequest();
        request.selectType("방문");
        assertEquals("방문", request.getType());
    }

    @Test
    public void 상담정보_입력이_정상_반영된다() {
        ConsultationRequest request = new ConsultationRequest();
        LocalDateTime scheduled = LocalDateTime.of(2026, 5, 10, 14, 0);
        request.selectType("방문");
        request.enterConsultationInfo(scheduled, "명지대 앞 카페", "010-1234-5678", "보험 상담 원합니다");

        assertEquals(scheduled, request.getScheduledAt());
        assertEquals("명지대 앞 카페", request.getLocation());
        assertEquals("010-1234-5678", request.getContact());
        assertEquals("보험 상담 원합니다", request.getContent());
    }

    @Test
    public void 필수항목_모두_입력시_검증_통과() {
        ConsultationRequest request = createCompleteRequest();
        assertTrue(request.validateRequiredFields());
    }

    @Test
    public void E1_상담유형_미입력시_검증_실패() {
        ConsultationRequest request = new ConsultationRequest();
        request.enterConsultationInfo(LocalDateTime.now(), "", "010-1234-5678", "상담 내용");
        assertFalse(request.validateRequiredFields());
    }

    @Test
    public void E1_연락처_미입력시_검증_실패() {
        ConsultationRequest request = new ConsultationRequest();
        request.selectType("방문");
        request.enterConsultationInfo(LocalDateTime.now(), "", "", "상담 내용");
        assertFalse(request.validateRequiredFields());
    }

    @Test
    public void E1_상담내용_미입력시_검증_실패() {
        ConsultationRequest request = new ConsultationRequest();
        request.selectType("방문");
        request.enterConsultationInfo(LocalDateTime.now(), "", "010-1234-5678", "");
        assertFalse(request.validateRequiredFields());
    }

    @Test
    public void submit_후_상태가_접수로_변경된다() {
        ConsultationRequest request = createCompleteRequest();
        request.submit();
        assertEquals("접수", request.getStatus());
    }

    @Test
    public void accept_후_상태가_수락으로_변경된다() {
        ConsultationRequest request = createCompleteRequest();
        request.submit();
        request.accept();
        assertEquals("수락", request.getStatus());
    }

    private ConsultationRequest createCompleteRequest() {
        ConsultationRequest request = new ConsultationRequest();
        request.selectType("방문");
        request.enterConsultationInfo(
                LocalDateTime.of(2026, 5, 10, 14, 0),
                "명지대 앞 카페",
                "010-1234-5678",
                "보험 상담 원합니다");
        return request;
    }
}