package dp.dao;

import dp.consultation.InterviewRecord;
import dp.db.DBA;
import java.util.List;

public class InterviewRecordDAO {

    public static void save(InterviewRecord r) {
        DBA.executeUpdate(
            "INSERT INTO interview_records (record_no, customer_name, content,"
            + " customer_reaction, follow_up_action, recorded_at)"
            + " VALUES (?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE content=VALUES(content),"
            + " customer_reaction=VALUES(customer_reaction),"
            + " follow_up_action=VALUES(follow_up_action),"
            + " recorded_at=VALUES(recorded_at)",
            String.valueOf(r.getRecordNumber()),
            r.getCustomerName(),
            r.getContent(),
            r.getCustomerReaction(),
            r.getFollowUpAction(),
            r.getInterviewedAt());
    }

    public static List<InterviewRecord> findAll() {
        return DBA.executeQuery(
            "SELECT record_no, customer_name, content, customer_reaction,"
            + " follow_up_action, recorded_at FROM interview_records",
            rs -> {
                String recordNo = rs.getString("record_no");
                int recordNumber = 0;
                if (recordNo != null) {
                    try { recordNumber = Integer.parseInt(recordNo); }
                    catch (NumberFormatException ignored) {}
                }
                java.sql.Timestamp ts = rs.getTimestamp("recorded_at");
                java.time.LocalDateTime interviewedAt = ts != null ? ts.toLocalDateTime() : null;
                return InterviewRecord.fromDb(
                        recordNumber,
                        rs.getString("customer_name"),
                        rs.getString("content"),
                        interviewedAt,
                        rs.getString("customer_reaction"),
                        rs.getString("follow_up_action"));
            });
    }
}
