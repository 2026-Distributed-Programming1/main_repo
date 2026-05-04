package dp.sales;

import dp.enums.ActivityType;

import java.util.Date;

/**
 * 일정 항목 (ScheduleItem)
 * 활동 계획에 포함되는 세부 일정 정보를 담는 클래스이다.
 */
public class ScheduleItem {
    private String customerId;
    private ActivityType activityType; // enum
    private Date activityDateTime;
    private String location;
    private String memo;

    public void delete() {}
}