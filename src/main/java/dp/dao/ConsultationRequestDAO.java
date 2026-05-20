package dp.dao;

import dp.consultation.ConsultationRequest;
import dp.db.DBA;

public class ConsultationRequestDAO {

    public static void save(ConsultationRequest r) {
        DBA.executeUpdate(
            "INSERT INTO consultation_requests (consult_no, channel, contact, content, status, requested_at)"
            + " VALUES (?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE channel=VALUES(channel),"
            + " contact=VALUES(contact), content=VALUES(content), status=VALUES(status)",
            String.valueOf(r.getConsultationNumber()),
            r.getType(),
            r.getContact(),
            r.getContent(),
            r.getStatus(),
            r.getScheduledAt());
    }
}