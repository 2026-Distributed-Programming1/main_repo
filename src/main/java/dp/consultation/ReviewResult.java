package dp.consultation;

import java.time.LocalDateTime;

/**
 * 심사결과 (ReviewResult)
 * UC: 심사 결과를 전달한다
 * Underwriting 과 Composition 관계
 */
public class ReviewResult {

    private String result;
    private String condition;
    private String rejectionReason;
    private LocalDateTime confirmedAt;

    public ReviewResult(String result, String condition, String rejectionReason) {
        this.result = result;
        this.condition = condition;
        this.rejectionReason = rejectionReason;
    }

    public void deliver() {
        System.out.println("  [시스템] 심사 결과가 판매채널에 전달되었습니다. 결과: " + result);
    }

    public void confirm() {
        this.confirmedAt = LocalDateTime.now();
        System.out.println("  [시스템] 심사 결과 확인이 완료되었습니다.");
    }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
}
