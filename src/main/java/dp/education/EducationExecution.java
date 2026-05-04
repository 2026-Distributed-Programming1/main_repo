package dp.education;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 교육진행 (EducationExecution)
 * UC: 교육을 진행한다
 */
public class EducationExecution {

    private static int sequence = 0;

    private int completionNumber;
    private LocalDateTime completedAt;
    private int attendanceCount;
    private int totalCount;
    private String memo;

    private final EducationPreparation preparation;

    public EducationExecution(EducationPreparation preparation) {
        sequence += 1;
        this.completionNumber = sequence;
        this.preparation = preparation;
    }

    public List<Attendance> loadAttendanceList() {
        return preparation.getAttendanceList();
    }

    public void markAttendance(String attendeeName, boolean isAttended) {
        for (Attendance a : preparation.getAttendanceList()) {
            if (a.getAttendeeName().equals(attendeeName)) {
                a.mark(isAttended);
                break;
            }
        }
    }

    public int calculateAttendanceCount() {
        List<Attendance> list = preparation.getAttendanceList();
        this.totalCount = list.size();
        this.attendanceCount = (int) list.stream().filter(Attendance::isAttended).count();
        return attendanceCount;
    }

    public void complete() {
        this.completedAt = LocalDateTime.now();
        calculateAttendanceCount();
    }

    public void sendCompletionNotice() {
        System.out.println("  [시스템] 판매채널에 수료 알림이 발송되었습니다.");
    }

    public int getCompletionNumber() { return completionNumber; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public int getAttendanceCount() { return attendanceCount; }
    public int getTotalCount() { return totalCount; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
