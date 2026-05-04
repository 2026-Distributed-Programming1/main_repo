package dp.sales;

import dp.enums.ChannelType;
import dp.enums.EvaluationGrade;

import java.util.Date;

/**
 * 영업 조직 평가 (SalesOrgEvaluation)
 * 실적을 바탕으로 판매채널의 성과를 평가하고 등급을 부여하는 클래스이다.
 */
public class SalesOrgEvaluation {
    private Date filterStartDate;
    private Date filterEndDate;
    private ChannelType channelType; // enum
    private String channelName;
    private Long salesResult;
    private Integer contractCount;
    private Double achievementRate;
    private EvaluationGrade evaluationGrade; // enum
    private String evaluationComment;
    private String evaluationNo;
    private Date evaluatedAt; // DateTime

    public void loadPerformanceTable() {}
    public void search() {}
    public void showNoResultMessage() {}
    public void sortByAchievementRate() {}
    public void openDetailPanel() {}
    public void closeDetailPanel() {}
    public void openEvaluationForm() {}
    public void validateRequired() { return; } // Boolean
    public void highlighterError() {}
    public void saveEvaluation() {}
    public void showEvaluationResult() {}
    public void cancelEvaluation() {}
    public void navigateToBonus() {}
}