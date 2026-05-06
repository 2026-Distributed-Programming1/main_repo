package dp.actor;

import dp.consultation.ConsultationRequest;
import dp.consultation.PolicyApplication;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * SalesChannel 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 채널ID, 채널명, 위치, 영업시작일 정상 설정
 * - acceptConsultation(): 상담 수락 → 상태 "수락"
 * - createPolicyApplication(): 청약서 객체 생성
 */
public class SalesChannelTest {

    @Test
    public void 생성_시_채널명이_정상_설정된다() {
        SalesChannel channel = new Designer(1, "최설계", "서울", "L-2024-001");
        assertEquals("최설계", channel.getName());
    }

    @Test
    public void 상담수락_후_상태가_수락으로_변경된다() {
        SalesChannel channel = new Designer(1, "최설계", "서울", "L-2024-001");
        ConsultationRequest request = new ConsultationRequest();
        request.selectType("방문");

        channel.acceptConsultation(request);

        assertEquals("수락", request.getStatus());
    }

    @Test
    public void createPolicyApplication이_청약서_객체를_반환한다() {
        SalesChannel channel = new Designer(1, "최설계", "서울", "L-2024-001");
        PolicyApplication application = channel.createPolicyApplication();
        assertNotNull(application);
    }
}