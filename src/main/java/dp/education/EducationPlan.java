package dp.education;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 교육계획안 (EducationPlan)
 * UC: 교육 계획안을 작성한다
 */
public class EducationPlan {

    private static int sequence = 0;

    private int planNumber;
    private String educationName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String channelType;
    private int targetCount;
    private long budget;
    private String status;

    public EducationPlan() {
        sequence += 1;
        this.planNumber = sequence;
        this.status = "작성중";
    }

    public void enterPlanInfo(String educationName, LocalDate startDate, LocalDate endDate,
                               String channelType, int targetCount, long budget) {
        this.educationName = educationName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.channelType = channelType;
        this.targetCount = targetCount;
        this.budget = budget;
    }

    public boolean validateRequiredFields() {
        return educationName != null && !educationName.isEmpty()
                && startDate != null && endDate != null
                && channelType != null && !channelType.isEmpty()
                && targetCount > 0 && budget > 0;
    }

    public void requestApproval() {
        this.status = "승인요청";
    }

    public void tempSave() {
        this.status = "임시저장";
    }

    public void approve() {
        this.status = "승인";
    }

    public void reject(String reason) {
        this.status = "반려";
    }

    public int getPlanNumber() { return planNumber; }
    public String getEducationName() { return educationName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getChannelType() { return channelType; }
    public int getTargetCount() { return targetCount; }
    public long getBudget() { return budget; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
