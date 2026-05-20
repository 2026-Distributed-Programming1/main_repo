package dp.dao;

import dp.db.DBA;
import dp.enums.ChannelType;
import dp.enums.ScreeningStatus;
import dp.sales.ChannelScreening;
import java.util.List;

public class ChannelScreeningDAO {

    public static void save(ChannelScreening s) {
        String screeningNo = s.getApprovalNo() != null ? s.getApprovalNo()
                : "CS-" + (s.getApplicantName() != null ? s.getApplicantName() : "")
                  + "-" + s.getApplicationDate();
        String channelType = s.getChannelType() != null ? s.getChannelType().name() : null;
        String status = s.getScreeningStatus() != null ? s.getScreeningStatus().name() : null;
        DBA.executeUpdate(
            "INSERT INTO channel_screenings (screening_no, candidate_name, channel_type,"
            + " qualification, status, reviewed_at)"
            + " VALUES (?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE status=VALUES(status)",
            screeningNo,
            s.getApplicantName(),
            channelType,
            s.getCareer(),
            status,
            s.getApprovedAt());
    }

    public static List<ChannelScreening> findAll() {
        return DBA.executeQuery(
            "SELECT screening_no, candidate_name, channel_type, qualification, status FROM channel_screenings",
            rs -> {
                ChannelScreening s = new ChannelScreening();
                s.setApplicantName(rs.getString("candidate_name"));
                String ct = rs.getString("channel_type");
                if (ct != null) {
                    try { s.setChannelType(ChannelType.valueOf(ct)); }
                    catch (IllegalArgumentException ignored) {}
                }
                s.setCareer(rs.getString("qualification"));
                String st = rs.getString("status");
                if (st != null) {
                    try { s.setScreeningStatus(ScreeningStatus.valueOf(st)); }
                    catch (IllegalArgumentException ignored) {}
                }
                return s;
            });
    }
}