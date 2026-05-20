package dp.dao;

import dp.consultation.ConsultationRequest;
import dp.db.DBA;
import java.util.List;

public class ConsultationRequestDAO {

    public static void save(ConsultationRequest r) {
        DBA.executeUpdate(
            "INSERT INTO consultation_requests (consult_no, channel, location, contact, content, status, requested_at)"
            + " VALUES (?,?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE channel=VALUES(channel), status=VALUES(status),"
            + " location=VALUES(location), contact=VALUES(contact), content=VALUES(content)",
            String.valueOf(r.getConsultationNumber()),
            r.getType(),
            r.getLocation(),
            r.getContact(),
            r.getContent(),
            r.getStatus(),
            r.getScheduledAt());
    }

    public static List<ConsultationRequest> findAll() {
        return DBA.executeQuery(
            "SELECT consult_no, channel, location, contact, content, status, requested_at"
            + " FROM consultation_requests",
            rs -> {
                String no = rs.getString("consult_no");
                int consultNo = 0;
                if (no != null) {
                    try { consultNo = Integer.parseInt(no); }
                    catch (NumberFormatException ignored) {}
                }
                java.sql.Timestamp ts = rs.getTimestamp("requested_at");
                java.time.LocalDateTime requestedAt = ts != null ? ts.toLocalDateTime() : null;
                return new ConsultationRequest(
                    consultNo,
                    rs.getString("channel"),
                    requestedAt,
                    rs.getString("location"),
                    rs.getString("contact"),
                    rs.getString("content"),
                    rs.getString("status"));
            });
    }
}
