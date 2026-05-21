package dp.dao;

import dp.consultation.ConsultationRequest;
import dp.db.DBA;
import java.util.List;

public class ConsultationRequestDAO {

    public static void save(ConsultationRequest r) {
        DBA.executeUpdate(
            "INSERT INTO consultation_requests (consult_no, channel, location, contact, content, status, requested_at, accepted_at)"
            + " VALUES (?,?,?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE channel=VALUES(channel), status=VALUES(status),"
            + " location=VALUES(location), contact=VALUES(contact), content=VALUES(content),"
            + " requested_at=VALUES(requested_at), accepted_at=VALUES(accepted_at)",
            String.valueOf(r.getConsultationNumber()),
            r.getType(),
            r.getLocation(),
            r.getContact(),
            r.getContent(),
            r.getStatus(),
            r.getReceivedAt(),
            r.getAcceptedAt());
    }

    public static List<ConsultationRequest> findAll() {
        return DBA.executeQuery(
            "SELECT consult_no, channel, location, contact, content, status, requested_at, accepted_at"
            + " FROM consultation_requests",
            rs -> {
                String no = rs.getString("consult_no");
                int consultNo = 0;
                if (no != null) {
                    try { consultNo = Integer.parseInt(no); }
                    catch (NumberFormatException ignored) {}
                }
                java.sql.Timestamp receivedTs = rs.getTimestamp("requested_at");
                java.time.LocalDateTime receivedAt = receivedTs != null ? receivedTs.toLocalDateTime() : null;
                java.sql.Timestamp acceptedTs = rs.getTimestamp("accepted_at");
                java.time.LocalDateTime acceptedAt = acceptedTs != null ? acceptedTs.toLocalDateTime() : null;
                ConsultationRequest cr = new ConsultationRequest(
                    consultNo,
                    rs.getString("channel"),
                    null,
                    rs.getString("location"),
                    rs.getString("contact"),
                    rs.getString("content"),
                    rs.getString("status"));
                cr.setReceivedAt(receivedAt);
                return cr;
            });
    }
}
