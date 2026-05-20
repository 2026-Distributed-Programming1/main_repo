package dp.dao;

import dp.db.DBA;
import dp.education.EducationPlan;
import java.util.List;

public class EducationPlanDAO {

    public static void save(EducationPlan p) {
        DBA.executeUpdate(
            "INSERT INTO education_plans (plan_no, trainer_name, title, target_audience,"
            + " scheduled_date, status)"
            + " VALUES (?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE status=VALUES(status)",
            String.valueOf(p.getPlanNumber()),
            p.getTrainerName(),
            p.getEducationName(),
            p.getChannelType(),
            p.getStartDate(),
            p.getStatus());
    }

    public static List<EducationPlan> findAll() {
        return DBA.executeQuery(
            "SELECT plan_no, trainer_name, title, target_audience, scheduled_date, status"
            + " FROM education_plans",
            rs -> {
                String planNo = rs.getString("plan_no");
                int planNumber = 0;
                if (planNo != null) {
                    try { planNumber = Integer.parseInt(planNo); }
                    catch (NumberFormatException ignored) {}
                }
                java.sql.Date sd = rs.getDate("scheduled_date");
                java.time.LocalDate startDate = sd != null ? sd.toLocalDate() : null;
                return EducationPlan.fromDb(
                        planNumber,
                        rs.getString("trainer_name"),
                        rs.getString("title"),
                        rs.getString("target_audience"),
                        startDate,
                        rs.getString("status"));
            });
    }
}
