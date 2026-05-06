package dp.sales;

import dp.enums.ActivityType;
import dp.enums.InsuranceType;
import dp.enums.PlanStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ActivityPlan 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 기본 상태(TEMP_SAVE), schedules 초기화
 * - validateDateRange(): 날짜 범위 유효성 검사
 * - validateRequired(): 필수 항목 검사
 * - addSchedule(): 일정 추가
 * - tempSave(): 임시 저장 시 planId, status 설정
 * - submit(): 제출 시 planId, status 설정
 */
public class ActivityPlanTest {

    @Test
    public void 생성_시_상태는_TEMP_SAVE이다() {
        ActivityPlan plan = new ActivityPlan();
        assertEquals(PlanStatus.TEMP_SAVE, plan.getStatus());
    }

    @Test
    public void 생성_시_일정목록이_초기화된다() {
        ActivityPlan plan = new ActivityPlan();
        assertNotNull(plan.getSchedules());
        assertTrue(plan.getSchedules().isEmpty());
    }

    @Test
    public void validateDateRange_시작일이_종료일보다_이전이면_true() {
        ActivityPlan plan = new ActivityPlan();
        plan.setStartDate(LocalDate.of(2025, 1, 1));
        plan.setEndDate(LocalDate.of(2025, 12, 31));
        assertTrue(plan.validateDateRange());
    }

    @Test
    public void validateDateRange_시작일과_종료일이_같으면_true() {
        ActivityPlan plan = new ActivityPlan();
        LocalDate today = LocalDate.now();
        plan.setStartDate(today);
        plan.setEndDate(today);
        assertTrue(plan.validateDateRange());
    }

    @Test
    public void validateDateRange_종료일이_시작일보다_이전이면_false() {
        ActivityPlan plan = new ActivityPlan();
        plan.setStartDate(LocalDate.of(2025, 12, 31));
        plan.setEndDate(LocalDate.of(2025, 1, 1));
        assertFalse(plan.validateDateRange());
    }

    @Test
    public void validateDateRange_날짜가_null이면_false() {
        ActivityPlan plan = new ActivityPlan();
        assertFalse(plan.validateDateRange());
    }

    @Test
    public void validateRequired_모든_필수값_입력시_true() {
        ActivityPlan plan = new ActivityPlan();
        plan.setPlanName("1분기 영업 계획");
        plan.setStartDate(LocalDate.of(2025, 1, 1));
        plan.setEndDate(LocalDate.of(2025, 3, 31));
        plan.setTargetContractCount(10);
        plan.setTargetContractAmount(5000000L);
        plan.setProposedCustomerId("CU-001");
        plan.setProposedInsuranceType(InsuranceType.LIFE);
        assertTrue(plan.validateRequired());
    }

    @Test
    public void validateRequired_계획명_누락시_false() {
        ActivityPlan plan = new ActivityPlan();
        plan.setStartDate(LocalDate.of(2025, 1, 1));
        plan.setEndDate(LocalDate.of(2025, 3, 31));
        plan.setTargetContractCount(10);
        plan.setTargetContractAmount(5000000L);
        plan.setProposedCustomerId("CU-001");
        plan.setProposedInsuranceType(InsuranceType.LIFE);
        assertFalse(plan.validateRequired());
    }

    @Test
    public void validateRequired_목표계약건수_0이하면_false() {
        ActivityPlan plan = new ActivityPlan();
        plan.setPlanName("계획");
        plan.setStartDate(LocalDate.now());
        plan.setEndDate(LocalDate.now().plusDays(30));
        plan.setTargetContractCount(0);
        plan.setTargetContractAmount(1000000L);
        plan.setProposedCustomerId("CU-001");
        plan.setProposedInsuranceType(InsuranceType.HEALTH);
        assertFalse(plan.validateRequired());
    }

    @Test
    public void validateRequired_목표계약금액_0이하면_false() {
        ActivityPlan plan = new ActivityPlan();
        plan.setPlanName("계획");
        plan.setStartDate(LocalDate.now());
        plan.setEndDate(LocalDate.now().plusDays(30));
        plan.setTargetContractCount(5);
        plan.setTargetContractAmount(0L);
        plan.setProposedCustomerId("CU-001");
        plan.setProposedInsuranceType(InsuranceType.HEALTH);
        assertFalse(plan.validateRequired());
    }

    @Test
    public void addSchedule_일정이_추가된다() {
        ActivityPlan plan = new ActivityPlan();
        ScheduleItem item = new ScheduleItem("CU-001", ActivityType.VISIT, "서울 강남", "첫 방문");
        plan.addSchedule(item);
        assertEquals(1, plan.getSchedules().size());
    }

    @Test
    public void addSchedule_null_추가시_무시된다() {
        ActivityPlan plan = new ActivityPlan();
        plan.addSchedule(null);
        assertTrue(plan.getSchedules().isEmpty());
    }

    @Test
    public void tempSave_후_planId가_부여된다() {
        ActivityPlan plan = new ActivityPlan();
        plan.tempSave();
        assertNotNull(plan.getPlanId());
        assertTrue(plan.getPlanId().startsWith("AP-TEMP-"));
    }

    @Test
    public void tempSave_후_상태는_TEMP_SAVE이다() {
        ActivityPlan plan = new ActivityPlan();
        plan.tempSave();
        assertEquals(PlanStatus.TEMP_SAVE, plan.getStatus());
    }

    @Test
    public void submit_후_planId가_부여된다() {
        ActivityPlan plan = new ActivityPlan();
        plan.submit();
        assertNotNull(plan.getPlanId());
        assertTrue(plan.getPlanId().startsWith("AP-"));
    }

    @Test
    public void submit_후_상태는_UNDER_REVIEW이다() {
        ActivityPlan plan = new ActivityPlan();
        plan.submit();
        assertEquals(PlanStatus.UNDER_REVIEW, plan.getStatus());
    }
}