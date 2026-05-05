package dp.contract;

import dp.enums.ContractStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 계약 (Contract)
 * 계약 정보를 조회하는 유스케이스의 주요 도메인 클래스이다.
 */
public class Contract {
    private String contractNo;              // 계약 번호
    private String insuranceType;           // 보험 종류
    private LocalDate startDate;            // 계약 시작일
    private LocalDate endDate;              // 계약 종료일
    private Long monthlyPremium;            // 월 보험료
    private ContractStatus status;          // 계약 상태 - 정상/만기 (enum)
    private Boolean isExpiringSoon;         // 만기 임박 여부
    private Integer totalPayCount;          // 전체 납입 횟수
    private Integer paidCount;              // 정상 납입 횟수
    private LocalDate lastPaymentDate;      // 최근 납입일
    private Boolean isOverdue;              // 연체 여부
    private Integer overdueCount;           // 연체 횟수
    private List<String> specialClauses;    // 특약명 목록
    private List<Long> clausePremiums;      // 특약 보험료 목록

    public Contract() {
        this.specialClauses = new ArrayList<>();
        this.clausePremiums = new ArrayList<>();
        this.status = ContractStatus.NORMAL;
        this.isExpiringSoon = false;
        this.isOverdue = false;
    }

    public void search() {}

    public void getDetail() {}

    public void edit() {}

    public void naviageToExpiry() {}

    public void navigateToStats() {}

    public void retry() {}

    public void showNoSpecialClause() {}

    public void showNotFoundMessage() {}

    // Runner에서 실제 사용하는 getter/setter만 유지
    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }
    public String getInsuranceType() { return insuranceType; }
    public void setInsuranceType(String insuranceType) { this.insuranceType = insuranceType; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Long getMonthlyPremium() { return monthlyPremium; }
    public void setMonthlyPremium(Long monthlyPremium) { this.monthlyPremium = monthlyPremium; }
    public ContractStatus getStatus() { return status; }
    public void setStatus(ContractStatus status) { this.status = status; }
    public Boolean getIsExpiringSoon() { return isExpiringSoon; }
    public void setIsExpiringSoon(Boolean isExpiringSoon) { this.isExpiringSoon = isExpiringSoon; }
    public Integer getTotalPayCount() { return totalPayCount; }
    public void setTotalPayCount(Integer totalPayCount) { this.totalPayCount = totalPayCount; }
    public Integer getPaidCount() { return paidCount; }
    public void setPaidCount(Integer paidCount) { this.paidCount = paidCount; }
    public void setLastPaymentDate(LocalDate lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }
    public Boolean getIsOverdue() { return isOverdue; }
    public void setIsOverdue(Boolean isOverdue) { this.isOverdue = isOverdue; }
    public Integer getOverdueCount() { return overdueCount; }
    public void setOverdueCount(Integer overdueCount) { this.overdueCount = overdueCount; }
    public List<String> getSpecialClauses() { return specialClauses; }
}