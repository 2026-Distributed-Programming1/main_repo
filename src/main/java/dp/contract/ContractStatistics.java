package dp.contract;

import dp.enums.PaymentStatus;
import dp.enums.ClaimStatus;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;
import java.io.File;

public class ContractStatistics {
    private String contractNo;
    private String contractorName;
    private Integer paySequence;
    private Date paymentDate;
    private Long paymentAmount;
    private PaymentStatus paymentStatus; // enum
    private Date claimDate; // DateTime
    private Long claimAmount;
    private Long paidAmount;
    private ClaimStatus claimStatus; // enum
    private List monthlyRetentionData;
    private YearMonth filterStartMonth;
    private YearMonth filterEndMonth;
    private String fileName;

    public void loadStatisticsPage() {}
    public void filterPaymentHistory() {}
    public Boolean validateDateRange() { return null; }
    public File exportToExcel() { return null; }
    public void loadGlobalStats() {}
    public void applyGlobalStats() {}
    public void showGlobalSummary() {}
    public void showNoResultMessage() {}
    public void selectContract() {}
    public void showDataRangeError() {}
    public String generateFileName() { return null; }
    public void setGlobalInsuranceType() {}
    public void setGlobalContractStatus() {}
    public void setGlobalDateRange() {}
}