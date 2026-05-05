package dp.sales;

import dp.enums.ChannelType;
import dp.enums.EvaluationGrade;

import java.util.Date;

/**
 * 성과급 지급 요청 (BonusRequest)
 * 평가 등급에 따라 성과급 지급을 요청하는 클래스이다.
 */
public class BonusRequest {
    private String evaluationNo;
    private String channelName;
    private ChannelType channelType; // enum
    private EvaluationGrade evaluationGrade; // enum
    private Double bonusRatio;
    private Long baseSalary;
    private Long bonusAmount;
    private String requestReason;
    private String requestNo;
    private Date requestedAt; // DateTime

    public void loadRequestScreen() {}
    public Long calculateBonus() { return null; }
    public void showConfirmPopup() {}
    public void submit() {}
    public void showRequestResult() {}
    public void showRequestError() {}
    public void retry() {}
    public void cancel() {}
    public void returnToEvaluation() {}
}