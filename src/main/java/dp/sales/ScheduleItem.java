package dp.sales;

import dp.enums.ActivityType;

import java.time.LocalDateTime;

/**
 * 일정 항목 (ScheduleItem)
 * 활동 계획에 포함되는 세부 일정 정보를 담는 클래스이다.
 */
public class ScheduleItem {
    private String customerId;              // 대상 고객번호 필수
    private ActivityType activityType;      // 활동 유형 - 방문/상담/전화 필수 (enum)
    private LocalDateTime activityDateTime; // 활동 일시 필수
    private String location;               // 활동 장소 선택
    private String memo;                   // 메모 선택

//    public ScheduleItem(String customerId, ActivityType activityType, String location, String memo) {
//        this.customerId = customerId;
//        this.activityType = activityType;
//        this.activityDateTime = LocalDateTime.now();
//        this.location = location;
//        this.memo = memo;
//    }

    public void delete() {}
    //getter,setter
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }
    public void setLocation(String location) { this.location = location; }
    public void setMemo(String memo) { this.memo = memo; }
}