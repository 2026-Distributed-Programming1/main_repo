package dp.dao;

import dp.actor.Designer;
import dp.db.DBA;
import java.util.List;

public class DesignerDAO {

    public static void save(Designer d) {
        DBA.executeUpdate(
            "INSERT INTO designers (channel_id, name, location, license_number)"
            + " VALUES (?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE name=VALUES(name)",
            d.getChannelId(), d.getName(), "", d.getLicenseNumber());
    }

    public static List<Designer> findAll() {
        return DBA.executeQuery(
            "SELECT channel_id, name, location, license_number FROM designers",
            rs -> new Designer(
                rs.getString("channel_id"),
                rs.getString("name"),
                rs.getString("location"),
                rs.getString("license_number")));
    }
}