package dp.sales;

import dp.enums.ChannelType;

import java.util.Date;

/**
 * 영업 활동 관리 (SalesActivityManagement)
 * 채널별 활동 현황을 모니터링하고 지표를 관리하는 클래스이다.
 */
public class SalesActivityManagement {
    private Date startDate;
    private Date endDate;
    private ChannelType channelType; // enum
    private String channelName;
    private Integer visitCount;
    private Integer contractCount;
    private Double conversionRate;
    private Double achievementRate;
    private String improvementContent;
    private Integer revisedTarget;
    private String managementNo;
    private Date registeredAt; // DateTime

    public void loadActivityTable() {}
    public void search() {}
    public void showNoResultMessage() {}
    public void sortByAchievementRate() {}
    public void highlightLowAchievement() {}
    public void openDetailPanel() {}
    public void closeDetailPanel() {}
    public void openImprovementForm() {}
    public void saveImprovement() {}
    public void showSaveSuccess() {}
    public void showSaveError() {}
    public void cancelImprovement() {}
    public void navigateToRecruitment() {}
}