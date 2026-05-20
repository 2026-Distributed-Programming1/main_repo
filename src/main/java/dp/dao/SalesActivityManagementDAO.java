package dp.dao;

import dp.db.DBA;
import dp.sales.SalesActivityManagement;
import java.util.List;

public class SalesActivityManagementDAO {

    public static void save(SalesActivityManagement a) {
        String channelType = a.getChannelType() != null ? a.getChannelType().name() : null;
        DBA.executeUpdate(
            "INSERT INTO sales_activity_managements (activity_no, manager_name, channel_name,"
            + " activity_type, created_at)"
            + " VALUES (?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE channel_name=VALUES(channel_name),"
            + " manager_name=VALUES(manager_name)",
            a.getManagementNo(),
            a.getManagerName(),
            a.getChannelName(),
            channelType,
            a.getRegisteredAt());
    }

    public static List<SalesActivityManagement> findAll() {
        return DBA.executeQuery(
            "SELECT activity_no, manager_name, channel_name, activity_type, created_at"
            + " FROM sales_activity_managements",
            rs -> {
                SalesActivityManagement a = new SalesActivityManagement();
                a.setManagerName(rs.getString("manager_name"));
                a.setChannelName(rs.getString("channel_name"));
                String ct = rs.getString("activity_type");
                if (ct != null) {
                    try { a.setChannelType(dp.enums.ChannelType.valueOf(ct)); }
                    catch (IllegalArgumentException ignored) {}
                }
                return a;
            });
    }
}