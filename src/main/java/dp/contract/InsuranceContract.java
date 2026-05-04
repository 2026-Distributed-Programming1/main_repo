package com.insurance.contract;

import com.insurance.actor.Customer;

import java.time.LocalDate;

/**
 * 보험계약 (InsuranceContract)
 *
 * 본 클래스는 6️⃣ 계약 관리 도메인에 속하지만, 7️⃣·8️⃣ 도메인의 클래스들이 참조하므로
 * 클래스 다이어그램에 명시된 필드와 메서드 중 본 도메인에서 활용되는 부분만 구현한다.
 *
 * 6️⃣ 도메인 자체의 모든 메서드(만기 관리, 통계, 갱신 등)는 별도 작업 시 구현한다.
 */
public class InsuranceContract {

    private static int sequence = 0;       // 계약번호 자동 부여용
    private static int policySequence = 0; // 증권번호 자동 부여용

    private String contractNo;             // 계약번호
    private Customer customer;             // 계약자
    // product, channel, paymentStatus, riders 등은 6️⃣ 도메인 구현 시 추가
    private LocalDate contractDate;        // 계약일자
    private LocalDate expiryDate;          // 만료일
    private long monthlyPremium;           // 월 보험료
    private String policyNo;               // 증권번호

    /** 생성자 - 계약번호·증권번호 자동 부여 (7,8 도메인용 단순화 버전) */
    public InsuranceContract(Customer customer, LocalDate contractDate, LocalDate expiryDate, long monthlyPremium) {
        sequence += 1;
        policySequence += 1;
        this.contractNo = "CON" + String.format("%05d", sequence);
        this.policyNo = "POL" + String.format("%05d", policySequence);
        this.customer = customer;
        this.contractDate = contractDate;
        this.expiryDate = expiryDate;
        this.monthlyPremium = monthlyPremium;
    }

    // Getter
    public String getContractNo() { return contractNo; }
    public Customer getCustomer() { return customer; }
    public LocalDate getContractDate() { return contractDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public long getMonthlyPremium() { return monthlyPremium; }
    public String getPolicyNo() { return policyNo; }
}
