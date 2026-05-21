package dp.dao;

import dp.db.DBA;
import dp.education.Attendance;
import dp.education.EducationExecution;

public class EducationExecutionDAO {

    public static void save(EducationExecution e) {
        String execNo = String.valueOf(e.getCompletionNumber());
        DBA.executeUpdate(
            "INSERT INTO education_executions (execution_no, prep_no, trainer_name,"
            + " executed_at, attendee_count, status)"
            + " VALUES (?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE attendee_count=VALUES(attendee_count)",
            execNo,
            e.getPreparation() != null ? String.valueOf(e.getPreparation().getSetupNumber()) : null,
            e.getPreparation() != null ? e.getPreparation().getInstructorName() : null,
            e.getCompletedAt(),
            e.getAttendanceCount(),
            null);

        if (e.getPreparation() != null) {
            for (Attendance a : e.getPreparation().getAttendanceList()) {
                DBA.executeUpdate(
                    "INSERT INTO education_attendances (execution_no, attendee_name, is_attended)"
                    + " VALUES (?,?,?)"
                    + " ON DUPLICATE KEY UPDATE is_attended=VALUES(is_attended)",
                    execNo,
                    a.getAttendeeName(),
                    a.isAttended());
            }
        }
    }
}