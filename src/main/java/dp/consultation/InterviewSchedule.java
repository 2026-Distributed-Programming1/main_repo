package dp.consultation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 면담일정 (InterviewSchedule)
 * UC: 면담일정을 관리한다
 */
public class InterviewSchedule {

    private static int sequence = 0;

    private int interviewNumber;
    private String customerName;
    private String type;
    private LocalDateTime scheduledAt;
    private String location;
    private String preparation;
    private String status;
    private List<InterviewRecord> interviewRecordList;

    public InterviewSchedule() {
        sequence += 1;
        this.interviewNumber = sequence;
        this.interviewRecordList = new ArrayList<>();
        this.status = "예정";
    }

    public void register(String customerName, LocalDateTime scheduledAt,
                          String location, String preparation) {
        this.customerName = customerName;
        this.scheduledAt = scheduledAt;
        this.location = location;
        this.preparation = preparation;
        this.status = "예정";
    }

    public void modify(LocalDateTime scheduledAt, String location, String preparation) {
        this.scheduledAt = scheduledAt;
        this.location = location;
        this.preparation = preparation;
    }

    public void cancel() {
        this.status = "취소";
    }

    public void sendNotice() {
        System.out.println("  [시스템] 고객에게 면담 일정 알림이 발송되었습니다.");
    }

    public boolean validateRequiredFields() {
        return customerName != null && !customerName.isEmpty() && scheduledAt != null;
    }

    public int getInterviewNumber() { return interviewNumber; }
    public String getCustomerName() { return customerName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public String getLocation() { return location; }
    public String getPreparation() { return preparation; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<InterviewRecord> getInterviewRecordList() { return interviewRecordList; }
}
