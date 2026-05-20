package dp.dao;

import dp.db.DBA;
import dp.enums.InquiryStatus;
import dp.enums.InquiryType;
import dp.inquiry.Inquiry;
import java.util.List;

public class InquiryDAO {

    public static void save(Inquiry i) {
        String inquiryType = i.getInquiryType() != null ? i.getInquiryType().name() : null;
        String status = i.getStatus() != null ? i.getStatus().name() : null;
        DBA.executeUpdate(
            "INSERT INTO inquiries (inquiry_no, customer_name, inquiry_type, title, content,"
            + " status, created_at)"
            + " VALUES (?,?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE status=VALUES(status)",
            i.getInquiryNo(),
            i.getCustomerName(),
            inquiryType,
            i.getTitle(),
            i.getContent(),
            status,
            i.getReceivedAt());
    }

    public static List<Inquiry> findAll() {
        return DBA.executeQuery(
            "SELECT inquiry_no, inquiry_type, title, content, status, created_at FROM inquiries ORDER BY created_at DESC",
            rs -> {
                String type = rs.getString("inquiry_type");
                InquiryType inquiryType = null;
                if (type != null) {
                    try { inquiryType = InquiryType.valueOf(type); }
                    catch (IllegalArgumentException ignored) {}
                }
                String statusStr = rs.getString("status");
                InquiryStatus status = null;
                if (statusStr != null) {
                    try { status = InquiryStatus.valueOf(statusStr); }
                    catch (IllegalArgumentException ignored) {}
                }
                java.time.LocalDateTime receivedAt = null;
                java.sql.Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) receivedAt = ts.toLocalDateTime();
                return new Inquiry(
                        rs.getString("inquiry_no"),
                        inquiryType,
                        rs.getString("title"),
                        rs.getString("content"),
                        receivedAt,
                        status);
            });
    }
}