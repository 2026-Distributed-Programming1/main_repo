package dp.sales;

import dp.enums.ChannelType;
import dp.enums.ScreeningStatus;

import java.util.Date;
import java.util.List;

/**
 * 판매채널 심사 (ChannelScreening)
 * 지원자의 자격과 경력을 심사하여 채용 여부를 결정하는 클래스이다.
 */
public class ChannelScreening {
    private String applicantName;
    private ChannelType channelType; // enum
    private Date applicationDate;
    private String career;
    private List<String> certifications;
    private ScreeningStatus screeningStatus; // enum
    private String approvalNo;
    private Date approvedAt; // DateTime
    private String rejectionReason;
    private Date filterStartDate;
    private Date filterEndDate;

    public void loadApplicantList() {}
    public void search() {}
    public void showNoResultMessage() {}
    public void openDetailPanel() {}
    public void showApprovalConfirm() {}
    public void approve() {}
    public void notifyHr() {}
    public void showApprovalResult() {}
    public void showNotifyError() {}
    public void showRegistrationError() {}
    public void openRejectionPopup() {}
    public void reject() {}
    public void closeDetailPanel() {}
}