package dp.dao;

import dp.db.DBA;
import dp.enums.EvaluationGrade;
import dp.sales.SalesOrgEvaluation;
import java.util.List;

public class SalesOrgEvaluationDAO {

    public static void save(SalesOrgEvaluation e) {
        String grade = e.getEvaluationGrade() != null ? e.getEvaluationGrade().name() : null;
        DBA.executeUpdate(
            "INSERT INTO sales_org_evaluations"
            + " (evaluation_no, org_name, grade, score,"
            + "  sales_result, contract_count, evaluation_comment, evaluated_at)"
            + " VALUES (?,?,?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE grade=VALUES(grade),"
            + "  sales_result=VALUES(sales_result),"
            + "  contract_count=VALUES(contract_count),"
            + "  evaluation_comment=VALUES(evaluation_comment)",
            e.getEvaluationNo(),
            e.getChannelName(),
            grade,
            e.getAchievementRate() != null ? e.getAchievementRate() : 0.0,
            e.getSalesResult() != null ? e.getSalesResult() : 0L,
            e.getContractCount() != null ? e.getContractCount() : 0,
            e.getEvaluationComment(),
            e.getEvaluatedAt());
    }

    public static List<SalesOrgEvaluation> findAll() {
        return DBA.executeQuery(
            "SELECT evaluation_no, org_name, grade, score,"
            + " sales_result, contract_count, evaluation_comment, evaluated_at"
            + " FROM sales_org_evaluations",
            rs -> {
                SalesOrgEvaluation e = new SalesOrgEvaluation();
                e.setChannelName(rs.getString("org_name"));
                String grade = rs.getString("grade");
                if (grade != null) {
                    try { e.setEvaluationGrade(EvaluationGrade.valueOf(grade)); }
                    catch (IllegalArgumentException ignored) {}
                }
                e.setAchievementRate(rs.getDouble("score"));
                e.setSalesResult(rs.getLong("sales_result"));
                e.setContractCount(rs.getInt("contract_count"));
                e.setEvaluationComment(rs.getString("evaluation_comment"));
                return e;
            });
    }
}