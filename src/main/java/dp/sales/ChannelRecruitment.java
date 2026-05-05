package dp.sales;

import dp.enums.ChannelType;

import java.util.Date;

/**
 * 판매채널 모집 (ChannelRecruitment)
 * 영업 관리자가 새로운 판매채널을 모집하기 위해 공고를 등록하고 관리하는 클래스이다.
 */
public class ChannelRecruitment {
    private String recruitmentNo;
    private ChannelType channelType; // enum
    private Integer recruitCount;
    private Date startDate;
    private Date endDate;
    private String condition;
    private Date registeredAt; // DateTime

    public void loadRecruitmentList() {}
    public void openRegistrationForm() {}
    public Boolean validateRequired() { return null; }
    public void highlighterError() {}
    public void save() {}
    public void showSaveSuccess() {}
    public void showSaveResult() {}
    public void cancel() {}
    public void showCancelConfirm() {}
    public void close() {}
    public void returnToActivityManagement() {}
    public void openCalendar() {}
    public void showRequiredError() {}
}