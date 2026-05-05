package dp.contract;

import dp.enums.ContractStatus;

import java.util.Date;
import java.util.List;

public class Contract {
    private String contractNo;
    private String insuranceType;
    private Date startDate;
    private Date endDate;
    private Long monthlyPremium;
    private ContractStatus status; // enum
    private Boolean isExpiringSoon;
    private Integer totalPayCount;
    private Integer paidCount;
    private Date lastPaymentDate;
    private Boolean isOverdue;
    private Integer overdueCount;
    private List<String> specialClauses;
    private List<Long> clausePremiums;

    public void search() {}
    public void getDetail() {}
    public void edit() {}
    public void naviageToExpiry() {}
    public void navigateToStats() {}
    public void retry() {}
    public void showNoSpecialClause() {}
    public void showNotFoundMessage() {}
}