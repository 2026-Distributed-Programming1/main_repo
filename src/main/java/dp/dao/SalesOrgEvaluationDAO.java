package dp.dao;

import dp.db.DBA;
import dp.enums.EvaluationGrade;
import dp.sales.SalesOrgEvaluation;
import java.util.List;

public class SalesOrgEvaluationDAO {

    public static void save(SalesOrgEvaluation e) {
        String grade = e.getEvaluationGrade() != null ? e.getEvaluationGrade().name() : null;
        DBA.executeUpdate(
            "INSERT INTO sales_org_evaluations (evaluation_no, org_name, grade, score, evaluated_at)"
            + " VALUES (?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE grade=VALUES(grade)",
            e.getEvaluationNo(),
            e.getChannelName(),
            grade,
            e.getAchievementRate() != null ? e.getAchievementRate() : 0.0,
            e.getEvaluatedAt());
    }

    public static List<SalesOrgEvaluation> findAll() {
        return DBA.executeQuery(
            "SELECT evaluation_no, org_name, grade, score, evaluated_at FROM sales_org_evaluations",
            rs -> {
                SalesOrgEvaluation e = new SalesOrgEvaluation();
                e.setChannelName(rs.getString("org_name"));
                String grade = rs.getString("grade");
                if (grade != null) {
                    try { e.setEvaluationGrade(EvaluationGrade.valueOf(grade)); }
                    catch (IllegalArgumentException ignored) {}
                }
                e.setAchievementRate(rs.getDouble("score"));
                return e;
            });
    }
}