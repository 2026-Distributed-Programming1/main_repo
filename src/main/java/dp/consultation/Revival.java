package dp.consultation;

import java.time.LocalDateTime;

/**
 * 부활신청 (Revival)
 * UC: 부활을 요청한다
 */
public class Revival {

    private static int sequence = 0;

    private int revivalNumber;
    private LocalDateTime appliedAt;
    private long unpaidAmount;
    private String paymentMethod;

    public Revival() {
        sequence += 1;
        this.revivalNumber = sequence;
    }

    public boolean checkEligibility() {
        System.out.println("  [시스템] 부활 가능 여부를 확인합니다.");
        return true;
    }

    public long calculateUnpaidAmount() {
        System.out.println("  [시스템] 미납보험료 및 이자를 산출합니다.");
        return unpaidAmount;
    }

    public void pay(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        System.out.println("  [시스템] 미납보험료가 납입되었습니다.");
    }

    public boolean authenticate() {
        System.out.println("  [시스템] 본인인증이 완료되었습니다.");
        return true;
    }

    public void submit() {
        this.appliedAt = LocalDateTime.now();
    }

    public int getRevivalNumber() { return revivalNumber; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public long getUnpaidAmount() { return unpaidAmount; }
    public void setUnpaidAmount(long unpaidAmount) { this.unpaidAmount = unpaidAmount; }
    public String getPaymentMethod() { return paymentMethod; }
}
