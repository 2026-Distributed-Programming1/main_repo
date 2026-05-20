package dp.db;

import java.lang.reflect.Field;

/**
 * JVM 재시작 후 도메인 클래스의 static sequence 값을 DB의 MAX PK 값에 맞춰 동기화한다.
 *
 * 문제: 도메인 클래스의 private static int sequence는 JVM이 재시작되면 0으로 초기화된다.
 * 이 상태에서 새 객체를 생성하면 sequence가 1부터 다시 시작하여 기존 DB 레코드와 PK가 충돌하고,
 * ON DUPLICATE KEY UPDATE로 인해 기존 데이터가 덮어써지는 문제가 발생한다.
 *
 * 해결: 리플렉션으로 private static 필드에 접근해 MAX(pk) 값을 직접 세팅한다.
 * 도메인 클래스의 시그니처(필드/메서드)를 변경하지 않기 위해 리플렉션을 사용한다.
 */
public class SequenceSync {

    private SequenceSync() {}

    public static void sync() {
        // actor
        set(dp.actor.Customer.class,  "customers",              "customer_id");
        setEmployee();

        // claim
        set(dp.claim.AccidentReport.class,      "accident_reports",      "accident_no");
        set(dp.claim.ClaimCalculation.class,    "claim_calculations",    "calculation_no");
        set(dp.claim.ClaimPayment.class,        "claim_payments",        "payment_no");
        set(dp.claim.ClaimRequest.class,        "claim_requests",        "claim_no");
        set(dp.claim.DamageInvestigation.class, "damage_investigations", "investigation_no");
        set(dp.claim.Dispatch.class,            "dispatches",            "dispatch_no");
        set(dp.claim.DispatchRecord.class,      "dispatch_records",      "record_no");

        // consultation
        set(dp.consultation.ConsultationRequest.class,  "consultation_requests",  "consult_no");
        set(dp.consultation.InsuranceApplication.class, "insurance_applications", "application_no");
        set(dp.consultation.InterviewRecord.class,      "interview_records",      "record_no");
        set(dp.consultation.InterviewSchedule.class,    "interview_schedules",    "schedule_no");
        set(dp.consultation.PolicyApplication.class,    "policy_applications",    "application_no");
        set(dp.consultation.Proposal.class,             "proposals",              "proposal_no");
        set(dp.consultation.Revival.class,              "revivals",               "revival_no");
        set(dp.consultation.Underwriting.class,         "underwritings",          "underwriting_no");

        // contract
        set(dp.contract.Cancellation.class, "cancellations", "cancellation_no");
        set(dp.contract.Contract.class,     "contracts",     "contract_no");

        // education
        set(dp.education.EducationExecution.class,   "education_executions",   "execution_no");
        set(dp.education.EducationPlan.class,        "education_plans",        "plan_no");
        set(dp.education.EducationPreparation.class, "education_preparations", "prep_no");

        // payment
        set(dp.payment.Payment.class,           "payments",         "payment_no");
        set(dp.payment.PaymentRecord.class,     "payment_records",  "record_no");
        set(dp.payment.RefundCalculation.class, "refund_calculations", "refund_no");
        set(dp.payment.RefundPayment.class,     "refund_payments",  "payment_no");
    }

    /** Employee 서브클래스들이 모두 Employee.sequence 를 공유하므로 여러 테이블에서 MAX를 구한다. */
    private static void setEmployee() {
        try {
            String maxStr = DBA.queryOne(
                "SELECT MAX(employee_id) FROM ("
                + " SELECT employee_id FROM claims_handlers"
                + " UNION ALL SELECT employee_id FROM finance_managers"
                + " UNION ALL SELECT employee_id FROM dispatch_agents"
                + " UNION ALL SELECT employee_id FROM insurance_reviewers"
                + " UNION ALL SELECT employee_id FROM education_trainers"
                + ") t",
                rs -> rs.getString(1));
            applyMax(dp.actor.Employee.class, parse(maxStr));
        } catch (Exception e) {
            System.err.println("[SequenceSync] Employee 실패: " + e.getMessage());
        }
    }

    private static void set(Class<?> clazz, String table, String pkCol) {
        try {
            String maxStr = DBA.queryOne(
                "SELECT MAX(" + pkCol + ") FROM " + table,
                rs -> rs.getString(1));
            applyMax(clazz, parse(maxStr));
        } catch (Exception e) {
            System.err.println("[SequenceSync] " + clazz.getSimpleName() + " 실패: " + e.getMessage());
        }
    }

    private static void applyMax(Class<?> clazz, int max) throws Exception {
        Field f = clazz.getDeclaredField("sequence");
        f.setAccessible(true);
        if ((int) f.get(null) < max) {
            f.set(null, max);
        }
    }

    private static int parse(String value) {
        if (value == null) return 0;
        String digits = value.replaceAll("[^0-9]", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }
}
