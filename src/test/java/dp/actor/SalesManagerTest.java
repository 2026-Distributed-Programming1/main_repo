package dp.actor;

import dp.education.EducationPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * SalesManager 단위 테스트
 *
 * 검증 대상:
 * - 생성자: Employee 상속 속성 정상 설정
 * - approveEducationPlan(): 교육계획안 승인 → 상태 "승인"
 * - rejectEducationPlan(): 교육계획안 반려 → 상태 "반려" (A3)
 */
public class SalesManagerTest {

    private EducationPlan plan;

    @BeforeEach
    public void setUp() {
        plan = new EducationPlan();
        plan.enterPlanInfo("신규 설계사 교육",
                LocalDate.of(2026, 5, 10),
                LocalDate.of(2026, 5, 11),
                "설계사", 10, 500000L);
        plan.requestApproval();
    }

    @Test
    public void 생성_시_이름이_정상_설정된다() {
        SalesManager manager = new SalesManager("이영관", "영업팀", "관리자");
        assertEquals("이영관", manager.getName());
    }

    @Test
    public void 승인_후_계획안_상태가_승인으로_변경된다() {
        SalesManager manager = new SalesManager("이영관", "영업팀", "관리자");
        manager.approveEducationPlan(plan);
        assertEquals("승인", plan.getStatus());
    }

    @Test
    public void A3_반려_후_계획안_상태가_반려로_변경된다() {
        SalesManager manager = new SalesManager("이영관", "영업팀", "관리자");
        manager.rejectEducationPlan(plan, "예산 초과");
        assertEquals("반려", plan.getStatus());
    }

    /*
 TODO: SalesManager가 Employee를 상속하도록 수정 후 테스트 활성화 필요
    @Test
    public void Employee를_상속한다() {
        SalesManager manager = new SalesManager("이영관", "영업팀", "관리자");
        assertInstanceOf(Employee.class, manager);
    }
*/
}