package dp.contract;

import java.time.LocalDateTime;

/**
 * 해지 (Cancellation)
 *
 * 본 클래스는 6️⃣ 계약 관리 도메인에 속하지만, 8️⃣ 도메인의 RefundCalculation이 참조하므로
 * 클래스 다이어그램에 명시된 필드와 메서드 중 본 도메인에서 활용되는 부분만 구현한다.
 *
 * 6️⃣ 도메인 자체의 모든 메서드(해지 절차, 본인인증 등)는 별도 작업 시 구현한다.
 */
public class Cancellation {

    private static int sequence = 0;     // 해지번호 자동 부여용

    private String cancellationNo;       // 해지번호
    private InsuranceContract contract;  // 대상 계약
    private long expectedRefund;         // 예상 환급금
    private LocalDateTime canceledAt;    // 해지일시

    /** 생성자 - 해지번호 자동 부여 (8 도메인용 단순화 버전) */
    public Cancellation(InsuranceContract contract) {
        sequence += 1;
        this.cancellationNo = "CAN" + String.format("%05d", sequence);
        this.contract = contract;
    }

    /** 해약 확정 - canceledAt=now() */
    public void confirm() {
        this.canceledAt = LocalDateTime.now();
    }

    /**
     * 예상 환급금 산출
     * 본 구현에서는 단순화하여 월 보험료의 12배로 설정 (실제 계산 로직은 6️⃣ 도메인에서 다룸)
     */
    public long calculateExpectedRefund() {
        if (contract != null) {
            this.expectedRefund = contract.getMonthlyPremium() * 12;
        }
        return this.expectedRefund;
    }

    // Getter
    public String getCancellationNo() { return cancellationNo; }
    public InsuranceContract getContract() { return contract; }
    public long getExpectedRefund() { return expectedRefund; }
    public LocalDateTime getCanceledAt() { return canceledAt; }
}
