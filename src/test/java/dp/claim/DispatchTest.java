package com.insurance.test.claim;

import com.insurance.actor.Customer;
import com.insurance.actor.DispatchAgent;
import com.insurance.claim.AccidentReport;
import com.insurance.claim.Dispatch;
import com.insurance.enums.DispatchStatus;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Dispatch 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 출동번호 자동 부여, 초기 상태 REQUESTED
 * - 상태 전이: 배정 → 출발 → 도착 → 완료
 * - A3: 위치 정보 갱신
 * - A4: 출동 취소
 */
public class DispatchTest {

    private AccidentReport accident;
    private DispatchAgent agent;

    @Before
    public void setUp() {
        Customer customer = new Customer("테스트고객", "900101-1234567", "010-0000-0000", "test@test.com");
        accident = new AccidentReport(customer);
        accident.enterLocation("최초 위치");
        agent = new DispatchAgent("출동직원", "현장출동팀", "사원", "강남", "12가3456");
    }

    @Test
    public void 생성_시_출동번호와_초기상태가_설정된다() {
        Dispatch dispatch = new Dispatch(accident);

        assertNotNull(dispatch.getDispatchNo());
        assertTrue(dispatch.getDispatchNo().startsWith("DSP"));
        assertEquals(DispatchStatus.REQUESTED, dispatch.getStatus());
        assertEquals(accident, dispatch.getAccident());
        assertNull(dispatch.getAgent());
    }

    @Test
    public void 직원_배정_후_상태가_ASSIGNED로_변경된다() {
        Dispatch dispatch = new Dispatch(accident);
        dispatch.assignAgent(agent);

        assertEquals(agent, dispatch.getAgent());
        assertEquals(DispatchStatus.ASSIGNED, dispatch.getStatus());
    }

    @Test
    public void 출발_후_상태가_DEPARTED로_변경된다() {
        Dispatch dispatch = new Dispatch(accident);
        dispatch.assignAgent(agent);
        dispatch.depart();

        assertEquals(DispatchStatus.DEPARTED, dispatch.getStatus());
    }

    @Test
    public void 도착_시_arrivalTime이_자동_설정된다() {
        Dispatch dispatch = new Dispatch(accident);
        dispatch.assignAgent(agent);
        dispatch.depart();
        dispatch.arrive();

        assertEquals(DispatchStatus.ARRIVED, dispatch.getStatus());
        assertNotNull(dispatch.getArrivalTime());
    }

    @Test
    public void 완료_후_상태가_COMPLETED로_변경된다() {
        Dispatch dispatch = new Dispatch(accident);
        dispatch.assignAgent(agent);
        dispatch.depart();
        dispatch.arrive();
        dispatch.complete();

        assertEquals(DispatchStatus.COMPLETED, dispatch.getStatus());
    }

    @Test
    public void 도착예정시간_설정() {
        Dispatch dispatch = new Dispatch(accident);
        LocalDateTime time = LocalDateTime.of(2025, 4, 26, 14, 30);
        dispatch.setEstimatedArrival(time);

        assertEquals(time, dispatch.getEstimatedArrival());
    }

    @Test
    public void A3_위치정보_갱신_시_원본_AccidentReport도_갱신된다() {
        Dispatch dispatch = new Dispatch(accident);
        dispatch.updateLocation("새 위치");

        assertEquals("새 위치", accident.getLocation());
    }

    @Test
    public void A4_출동_취소_시_상태와_사유_저장() {
        Dispatch dispatch = new Dispatch(accident);
        dispatch.cancel("고객 자체 처리");

        assertEquals(DispatchStatus.CANCELED, dispatch.getStatus());
        assertEquals("고객 자체 처리", dispatch.getCancelReason());
    }
}
