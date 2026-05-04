package com.insurance.actor;

/**
 * 보상담당자 (ClaimsHandler)
 *
 * 보험금 처리의 핵심 행위자로, 손해 조사부터 산출, 지급까지 전 과정을 담당한다.
 * 전결 한도(transferLimit)를 가지며, 산출액이 이 한도를 초과하면 결재 상신이 필요하다.
 */
public class ClaimsHandler extends Employee {

    private long transferLimit;    // 전결 한도

    /** 생성자 */
    public ClaimsHandler(String name, String dept, String position, long limit) {
        super(name, dept, position);
        this.transferLimit = limit;
    }

    // 7️⃣ 도메인 메서드들은 시나리오 추적성을 위한 진입점이지만,
    // 실제 비즈니스 로직은 도메인 객체(DamageInvestigation, ClaimCalculation, ClaimPayment 등)에 있다.
    // 본 구현에서는 행위자 메서드를 본문 없이 정의하지 않고, 호출 측(Main)에서 직접
    // 도메인 객체의 메서드를 호출하는 방식으로 진행한다.

    // Getter
    public long getTransferLimit() { return transferLimit; }
}
