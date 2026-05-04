package dp.actor;

import dp.education.EducationExecution;
import dp.education.EducationPlan;
import dp.education.EducationPreparation;

/**
 * 영업교육담당자 (EducationTrainer)
 */
public class EducationTrainer extends Employee {

    public EducationTrainer(String name, String contact, String email) {
        super(name, contact, email);
    }

    public EducationPlan createEducationPlan() {
        return new EducationPlan();
    }

    public EducationPreparation registerEducationPreparation() {
        return new EducationPreparation();
    }

    public EducationExecution conductEducation(EducationPreparation preparation) {
        return new EducationExecution(preparation);
    }
}
