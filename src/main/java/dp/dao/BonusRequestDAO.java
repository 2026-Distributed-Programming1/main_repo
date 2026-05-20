package dp.dao;

import dp.db.DBA;
import dp.sales.BonusRequest;

public class BonusRequestDAO {

    public static void save(BonusRequest r) {
        DBA.executeUpdate(
            "INSERT INTO bonus_requests (request_no, requester, amount, reason, status, created_at)"
            + " VALUES (?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE amount=VALUES(amount)",
            r.getRequestNo(),
            r.getChannelName(),
            r.getBonusAmount() != null ? r.getBonusAmount().longValue() : 0L,
            r.getRequestReason(),
            "SUBMITTED",
            r.getRequestedAt());
    }
}