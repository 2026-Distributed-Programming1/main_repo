package dp.dao;

import dp.consultation.Underwriting;
import dp.db.DBA;

public class UnderwritingDAO {

    public static void save(Underwriting u) {
        String result = u.getReviewResult() != null ? u.getReviewResult().getResult() : null;
        DBA.executeUpdate(
            "INSERT INTO underwritings (underwriting_no, app_type, app_no, customer_name,"
            + " result, reviewed_at)"
            + " VALUES (?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE result=VALUES(result)",
            String.valueOf(u.getReviewNumber()),
            u.getReviewType(),
            u.getAppNo(),
            u.getCustomerName(),
            result,
            u.getReviewedAt());
    }
}