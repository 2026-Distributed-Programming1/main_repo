package dp.actor;

import dp.education.EducationPlan;

/**
 * 영업관리자 (SalesManager)
 */
public class SalesManager extends Employee {

    public SalesManager(String name, String contact, String email) {
        super(name, contact, email);
    }

    public void approveEducationPlan(EducationPlan plan) {
        plan.setStatus("승인");
        System.out.println("  [영업관리자] 교육계획안이 승인되었습니다.");
    }

    public void rejectEducationPlan(EducationPlan plan, String reason) {
        plan.reject(reason);
        System.out.println("  [영업관리자] 교육계획안이 반려되었습니다. 사유: " + reason);
    }
}
