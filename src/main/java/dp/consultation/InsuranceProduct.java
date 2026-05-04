package dp.consultation;

/**
 * 보험상품 (InsuranceProduct)
 * UC: 보험상품을 조회한다, 보험상품을 제안한다
 */
public class InsuranceProduct {

    private String productName;
    private String type;
    private long monthlyPremium;
    private String coverage;
    private String specialTerms;

    public InsuranceProduct(String productName, String type, long monthlyPremium,
                             String coverage, String specialTerms) {
        this.productName = productName;
        this.type = type;
        this.monthlyPremium = monthlyPremium;
        this.coverage = coverage;
        this.specialTerms = specialTerms;
    }

    public String getProductName() { return productName; }
    public String getType() { return type; }
    public long getMonthlyPremium() { return monthlyPremium; }
    public String getCoverage() { return coverage; }
    public String getSpecialTerms() { return specialTerms; }
}
