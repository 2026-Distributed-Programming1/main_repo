package dp.sales;

import dp.enums.InsuranceType;
import dp.enums.PlanStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 활동 계획 (ActivityPlan)
 * 판매채널이 작성하는 영업 활동 계획서 클래스이다.
 */
public class ActivityPlan {
    private String planId;                       // 계획 ID
    private String planName;                     // 계획명 필수
    private LocalDate startDate;                 // 시작일 필수
    private LocalDate endDate;                   // 종료일 필수
    private String author;                       // 작성자 - 자동입력
    private String memo;                         // 메모 선택
    private List<ScheduleItem> schedules;        // 일정 목록
    private Integer targetContractCount;         // 목표 계약 건수 필수
    private Long targetContractAmount;           // 목표 계약 금액 필수
    private Integer targetNewCustomer;           // 목표 신규 고객 수 선택
    private String proposedCustomerId;           // 제안 대상 고객번호 필수
    private InsuranceType proposedInsuranceType; // 제안 보험 종류 필수 (enum)
    private String proposalReason;               // 제안 사유 선택
    private PlanStatus status;                   // 계획 상태 - 임시저장/검토중 (enum)

    public ActivityPlan() {
        this.schedules = new ArrayList<>();
        this.status = PlanStatus.TEMP_SAVE;
    }

    public Boolean validateDateRange() {
        if (startDate == null || endDate == null) return false;
        return !endDate.isBefore(startDate);
    }
    public void addSchedule(ScheduleItem item) {
        if (item != null) {
            this.schedules.add(item);
        }
    }

    public void deleteSchedule() {}

    public void sortSchedules() {}

    public Boolean validateRequired() {
        return planName != null && !planName.isEmpty()
                && startDate != null
                && endDate != null
                && targetContractCount != null && targetContractCount > 0
                && targetContractAmount != null && targetContractAmount > 0
                && proposedCustomerId != null && !proposedCustomerId.isEmpty()
                && proposedInsuranceType != null;
    }

    public void highlightError() {}

    public void tempSave() {
        this.status = PlanStatus.TEMP_SAVE;
        this.planId = "AP-TEMP-" + LocalDateTime.now().toString().replaceAll("[^0-9]", "").substring(0, 14);
    }

    public void submit() {
        this.status = PlanStatus.UNDER_REVIEW;
        this.planId = "AP-" + LocalDateTime.now().toString().replaceAll("[^0-9]", "").substring(0, 14);
    }

    public void notifyManager() {}

    public void showTempSaveMessage() {}

    public void showSubmitMessage() {}

    public void showDateRangeError() {}

    public void openCalendar() {}

    public void retainValues() {}

    // Runner에서 실제 사용하는 getter/setter만 유지
    public String getPlanId() { return planId; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setAuthor(String author) { this.author = author; }
    public void setMemo(String memo) { this.memo = memo; }
    public List<ScheduleItem> getSchedules() { return schedules; }
    public Integer getTargetContractCount() { return targetContractCount; }
    public void setTargetContractCount(Integer targetContractCount) { this.targetContractCount = targetContractCount; }
    public Long getTargetContractAmount() { return targetContractAmount; }
    public void setTargetContractAmount(Long targetContractAmount) { this.targetContractAmount = targetContractAmount; }
    public void setTargetNewCustomer(Integer targetNewCustomer) { this.targetNewCustomer = targetNewCustomer; }
    public void setProposedCustomerId(String proposedCustomerId) { this.proposedCustomerId = proposedCustomerId; }
    public InsuranceType getProposedInsuranceType() { return proposedInsuranceType; }
    public void setProposedInsuranceType(InsuranceType proposedInsuranceType) { this.proposedInsuranceType = proposedInsuranceType; }
    public void setProposalReason(String proposalReason) { this.proposalReason = proposalReason; }
    public PlanStatus getStatus() { return status; }
}