package dp.dao;

import dp.db.DBA;
import dp.education.EducationExecution;

public class EducationExecutionDAO {

    public static void save(EducationExecution e) {
        DBA.executeUpdate(
            "INSERT INTO education_executions (execution_no, prep_no, trainer_name,"
            + " executed_at, attendee_count, status)"
            + " VALUES (?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE attendee_count=VALUES(attendee_count)",
            String.valueOf(e.getCompletionNumber()),
            e.getPreparation() != null ? String.valueOf(e.getPreparation().getSetupNumber()) : null,
            e.getPreparation() != null ? e.getPreparation().getInstructorName() : null,
            e.getCompletedAt(),
            e.getAttendanceCount(),
            null);
    }
}