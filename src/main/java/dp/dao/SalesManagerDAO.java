package dp.dao;

import dp.actor.SalesManager;
import dp.db.DBA;
import java.util.List;

public class SalesManagerDAO {

    public static void save(SalesManager m) {
        DBA.executeUpdate(
            "INSERT INTO sales_managers (manager_id, name, department)"
            + " VALUES (?,?,?)"
            + " ON DUPLICATE KEY UPDATE name=VALUES(name)",
            m.getManagerId(), m.getName(), m.getDepartment());
    }

    public static List<SalesManager> findAll() {
        return DBA.executeQuery(
            "SELECT manager_id, name, department FROM sales_managers",
            rs -> SalesManager.fromDb(
                rs.getString("manager_id"),
                rs.getString("name"),
                rs.getString("department")));
    }
}