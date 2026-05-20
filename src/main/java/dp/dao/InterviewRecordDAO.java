package dp.dao;

import dp.consultation.InterviewRecord;
import dp.db.DBA;
import java.util.List;

public class InterviewRecordDAO {

    public static void save(InterviewRecord r) {
        DBA.executeUpdate(
            "INSERT INTO interview_records (record_no, customer_name, content, recorded_at)"
            + " VALUES (?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE content=VALUES(content)",
            String.valueOf(r.getRecordNumber()),
            r.getCustomerName(),
            r.getContent(),
            r.getInterviewedAt());
    }

    public static List<InterviewRecord> findAll() {
        return DBA.executeQuery(
            "SELECT record_no, customer_name, content FROM interview_records",
            rs -> {
                String recordNo = rs.getString("record_no");
                int recordNumber = 0;
                if (recordNo != null) {
                    try { recordNumber = Integer.parseInt(recordNo); }
                    catch (NumberFormatException ignored) {}
                }
                return InterviewRecord.fromDb(
                        recordNumber,
                        rs.getString("customer_name"),
                        rs.getString("content"));
            });
    }
}
