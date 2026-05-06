package dp.actor;

import dp.education.EducationExecution;
import dp.education.EducationPlan;
import dp.education.EducationPreparation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * EducationTrainer 단위 테스트
 *
 * 검증 대상:
 * - 생성자: Employee 상속 속성 정상 설정
 * - createEducationPlan(): EducationPlan 객체 생성
 * - registerEducationPreparation(): EducationPreparation 객체 생성
 * - conductEducation(): EducationExecution 객체 생성
 */
public class EducationTrainerTest {

    @Test
    public void 생성_시_이름이_정상_설정된다() {
        EducationTrainer trainer = new EducationTrainer("김영교", "교육팀", "담당자");
        assertEquals("김영교", trainer.getName());
    }

    @Test
    public void createEducationPlan이_EducationPlan을_반환한다() {
        EducationTrainer trainer = new EducationTrainer("김영교", "교육팀", "담당자");
        EducationPlan plan = trainer.createEducationPlan();
        assertNotNull(plan);
    }

    @Test
    public void registerEducationPreparation이_EducationPreparation을_반환한다() {
        EducationTrainer trainer = new EducationTrainer("김영교", "교육팀", "담당자");
        EducationPreparation preparation = trainer.registerEducationPreparation();
        assertNotNull(preparation);
    }

    @Test
    public void conductEducation이_EducationExecution을_반환한다() {
        EducationTrainer trainer = new EducationTrainer("김영교", "교육팀", "담당자");
        EducationPreparation preparation = new EducationPreparation();
        preparation.enterPreparationInfo("명지대", "김강사", "");

        EducationExecution execution = trainer.conductEducation(preparation);
        assertNotNull(execution);
    }

    @Test
    public void Employee를_상속한다() {
        EducationTrainer trainer = new EducationTrainer("김영교", "교육팀", "담당자");
        assertInstanceOf(Employee.class, trainer);
    }
}