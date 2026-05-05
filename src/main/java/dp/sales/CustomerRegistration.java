package dp.sales;

import dp.enums.InsuranceType;

import java.util.Date;
import java.util.List;

/**
 * 고객 등록 (CustomerRegistration)
 * 판매채널이 확보한 고객 정보를 시스템에 최초 등록하는 클래스이다.
 */
public class CustomerRegistration {
    private String customerId;
    private String name;
    private String ssn;
    private Boolean isSsnMasked;
    private String phone;
    private String address;
    private String contractNo;
    private InsuranceType insuranceType; // enum
    private Date contractDate;
    private Date expiryDate;
    private Long monthlyPremium;
    private List<String> specialClauses;

    public void toggleSsnMask() {}
    public void searchAddress() {}
    public void addSpecialClause() {}
    public void removeSpecialClause() {}
    public Boolean validateRequired() { return null; }
    public Boolean validateFormat() { return null; }
    public Boolean validateDuplicate() { return null; }
    public void highlighterError() {}
    public void showDuplicateError() {}
    public void assignIds() {}
    public void showSuccessPopup() {}
    public void save() {}
    public void edit() {}
    public void openCalendar() {}
}