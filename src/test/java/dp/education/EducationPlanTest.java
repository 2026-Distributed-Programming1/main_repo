package dp.education;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * EducationPlan 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 계획번호 자동 부여, 초기 상태 "작성중"
 * - enterPlanInfo(): 필드 정상 반영
 * - validateRequiredFields(): 필수항목 검증 (E1)
 * - requestApproval(): 상태 "승인요청" 변경
 * - tempSave(): 상태 "임시저장" 변경 (A1)
 * - reject(): 상태 "반려" 변경 (A3)
 */
public class EducationPlanTest {

    @Test
    public void 생성_시_계획번호와_초기상태가_설정된다() {
        EducationPlan plan = new EducationPlan();

        assertTrue(plan.getPlanNumber() > 0);
        assertEquals("작성중", plan.getStatus());
    }

    @Test
    public void 기본정보_입력이_정상_반영된다() {
        EducationPlan plan = new EducationPlan();
        LocalDate start = LocalDate.of(2026, 5, 10);
        LocalDate end = LocalDate.of(2026, 5, 11);

        plan.enterPlanInfo("신규 설계사 교육", start, end, "설계사", 10, 500000L);

        assertEquals("신규 설계사 교육", plan.getEducationName());
        assertEquals("설계사", plan.getChannelType());
        assertEquals(10, plan.getTargetCount());
        assertEquals(500000L, plan.getBudget());
        assertEquals(start, plan.getStartDate());
        assertEquals(end, plan.getEndDate());
    }

    @Test
    public void 필수항목_모두_입력시_검증_통과() {
        EducationPlan plan = createCompletePlan();
        assertTrue(plan.validateRequiredFields());
    }

    @Test
    public void 교육명_미입력시_검증_실패() {
        EducationPlan plan = new EducationPlan();
        plan.enterPlanInfo("", LocalDate.now(), LocalDate.now().plusDays(1), "설계사", 10, 500000L);
        assertFalse(plan.validateRequiredFields());
    }

    @Test
    public void 대상자수_0이면_검증_실패() {
        EducationPlan plan = new EducationPlan();
        plan.enterPlanInfo("교육명", LocalDate.now(), LocalDate.now().plusDays(1), "설계사", 0, 500000L);
        assertFalse(plan.validateRequiredFields());
    }

    @Test
    public void 예산_0이면_검증_실패() {
        EducationPlan plan = new EducationPlan();
        plan.enterPlanInfo("교육명", LocalDate.now(), LocalDate.now().plusDays(1), "설계사", 10, 0L);
        assertFalse(plan.validateRequiredFields());
    }

    @Test
    public void 승인요청_시_상태가_변경된다() {
        EducationPlan plan = createCompletePlan();
        plan.requestApproval();
        assertEquals("승인요청", plan.getStatus());
    }

    @Test
    public void A1_임시저장_시_상태가_변경된다() {
        EducationPlan plan = createCompletePlan();
        plan.tempSave();
        assertEquals("임시저장", plan.getStatus());
    }

    @Test
    public void A3_반려_시_상태가_변경된다() {
        EducationPlan plan = createCompletePlan();
        plan.requestApproval();
        plan.reject("예산 초과");
        assertEquals("반려", plan.getStatus());
    }

    @Test
    public void setStatus로_승인_처리된다() {
        EducationPlan plan = createCompletePlan();
        plan.setStatus("승인");
        assertEquals("승인", plan.getStatus());
    }

    private EducationPlan createCompletePlan() {
        EducationPlan plan = new EducationPlan();
        plan.enterPlanInfo("신규 설계사 교육",
                LocalDate.of(2026, 5, 10),
                LocalDate.of(2026, 5, 11),
                "설계사", 10, 500000L);
        return plan;
    }
}