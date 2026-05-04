package dp.sales;

import dp.enums.InsuranceType;
import dp.enums.PlanStatus;

import java.util.Date;
import java.util.List;

/**
 * 활동 계획 (ActivityPlan)
 * 판매채널이 작성하는 영업 활동 계획서 클래스이다.
 */
public class ActivityPlan {
    private String planId;
    private String planName;
    private Date startDate;
    private Date endDate;
    private String author;
    private String memo;
    private List<ScheduleItem> schedules;
    private Integer targetContractCount;
    private Long targetContractAmount;
    private Integer targetNewCustomer;
    private String proposedCustomerId;
    private InsuranceType proposedInsuranceType; // enum
    private String proposalReason;
    private PlanStatus status; // enum

    public Boolean validateDateRange() { return null; }
    public void addSchedule() {}
    public void deleteSchedule() {}
    public void sortSchedules() {}
    public Boolean validateRequired() { return null; }
    public void highlighterError() {}
    public void tempSave() {}
    public void submit() {}
    public void notifyManager() {}
    public void showTempSaveMessage() {}
    public void showSubmitMessage() {}
    public void showDateRangeError() {}
    public void openCalendar() {}
    public void retainValues() {}
}