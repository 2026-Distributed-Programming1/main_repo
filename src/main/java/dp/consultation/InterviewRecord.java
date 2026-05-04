package dp.consultation;

import java.time.LocalDateTime;

/**
 * 면담기록 (InterviewRecord)
 * UC: 면담기록을 관리한다
 */
public class InterviewRecord {

    private static int sequence = 0;

    private int recordNumber;
    private String customerName;
    private LocalDateTime interviewedAt;
    private String content;
    private String customerReaction;
    private String followUpAction;

    public InterviewRecord() {
        sequence += 1;
        this.recordNumber = sequence;
    }

    public void save(String content, String customerReaction, String followUpAction) {
        this.content = content;
        this.customerReaction = customerReaction;
        this.followUpAction = followUpAction;
        this.interviewedAt = LocalDateTime.now();
    }

    public void modify(String content, String customerReaction, String followUpAction) {
        this.content = content;
        this.customerReaction = customerReaction;
        this.followUpAction = followUpAction;
    }

    public boolean validateRequiredFields() {
        return content != null && !content.isEmpty()
                && customerReaction != null && !customerReaction.isEmpty();
    }

    public Proposal navigateToProposal() {
        return new Proposal();
    }

    public int getRecordNumber() { return recordNumber; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public LocalDateTime getInterviewedAt() { return interviewedAt; }
    public String getContent() { return content; }
    public String getCustomerReaction() { return customerReaction; }
    public String getFollowUpAction() { return followUpAction; }
}
