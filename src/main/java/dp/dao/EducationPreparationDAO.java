package dp.dao;

import dp.db.DBA;
import dp.education.EducationPreparation;
import java.util.ArrayList;
import java.util.List;

public class EducationPreparationDAO {

    public static void save(EducationPreparation e) {
        DBA.executeUpdate(
            "INSERT INTO education_preparations (prep_no, plan_no, trainer_name, venue,"
            + " material_ready, status)"
            + " VALUES (?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE venue=VALUES(venue), status=VALUES(status)",
            String.valueOf(e.getSetupNumber()),
            e.getPlanNo(),
            e.getInstructorName(),
            e.getLocation(),
            e.getTextbookStatus() != null,
            null);
    }

    public static List<EducationPreparation> findAll() {
        return DBA.executeQuery(
            "SELECT prep_no, plan_no, trainer_name, venue FROM education_preparations",
            rs -> {
                EducationPreparation e = new EducationPreparation(
                    rs.getInt("prep_no"),
                    null,
                    rs.getString("venue"),
                    rs.getString("trainer_name"),
                    null,
                    null,
                    new ArrayList<>());
                e.setPlanNo(rs.getString("plan_no"));
                return e;
            });
    }
}
