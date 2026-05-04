package dp.education;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 교육제반 (EducationPreparation)
 * UC: 교육 제반을 등록한다
 */
public class EducationPreparation {

    private static int sequence = 0;

    private int setupNumber;
    private LocalDateTime registeredAt;
    private String location;
    private String instructorName;
    private String textbookStatus;
    private String additionalNotice;
    private List<Attendance> attendanceList;

    public EducationPreparation() {
        sequence += 1;
        this.setupNumber = sequence;
        this.attendanceList = new ArrayList<>();
    }

    public void enterPreparationInfo(String location, String instructorName, String additionalNotice) {
        this.location = location;
        this.instructorName = instructorName;
        this.additionalNotice = additionalNotice;
    }

    public boolean validateRequiredFields() {
        return location != null && !location.isEmpty()
                && instructorName != null && !instructorName.isEmpty();
    }

    public void save() {
        this.registeredAt = LocalDateTime.now();
    }

    public void addAttendee(String attendeeName) {
        attendanceList.add(new Attendance(attendeeName));
    }

    public int getSetupNumber() { return setupNumber; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public String getLocation() { return location; }
    public String getInstructorName() { return instructorName; }
    public String getTextbookStatus() { return textbookStatus; }
    public void setTextbookStatus(String textbookStatus) { this.textbookStatus = textbookStatus; }
    public String getAdditionalNotice() { return additionalNotice; }
    public List<Attendance> getAttendanceList() { return attendanceList; }
}
