package dp.dao;

import dp.consultation.Revival;
import dp.db.DBA;

public class RevivalDAO {

    public static void save(Revival r) {
        DBA.executeUpdate(
            "INSERT INTO revivals (revival_no, contract_no, customer_name, revived_at)"
            + " VALUES (?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE revived_at=VALUES(revived_at)",
            String.valueOf(r.getRevivalNumber()),
            null,
            r.getCustomer() != null ? r.getCustomer().getName() : null,
            r.getAppliedAt());
    }
}