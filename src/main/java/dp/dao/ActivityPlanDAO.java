package dp.dao;

import dp.db.DBA;
import dp.sales.ActivityPlan;

public class ActivityPlanDAO {

    public static void save(ActivityPlan p) {
        String status = p.getStatus() != null ? p.getStatus().name() : null;
        String insuranceType = p.getProposedInsuranceType() != null
                ? p.getProposedInsuranceType().name() : null;
        DBA.executeUpdate(
            "INSERT INTO activity_plans (plan_no, author_name, activity_type, scheduled_date,"
            + " target, status)"
            + " VALUES (?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE status=VALUES(status)",
            p.getPlanId(),
            p.getAuthor(),
            insuranceType,
            p.getStartDate(),
            p.getProposedCustomerId(),
            status);
    }
}