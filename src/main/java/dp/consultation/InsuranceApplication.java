package dp.consultation;

import dp.actor.Customer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 보험신청 (InsuranceApplication)
 * UC: 보험을 신청한다
 */
public class InsuranceApplication {

    private static int sequence = 0;

    private int applicationNumber;
    private Customer customer;
    private InsuranceProduct product;
    private LocalDateTime appliedAt;
    private String paymentMethod;
    private List<String> selectedSpecialTerms;

    public InsuranceApplication() {
        sequence += 1;
        this.applicationNumber = sequence;
        this.selectedSpecialTerms = new ArrayList<>();
    }

    public void setCustomer(Customer customer) { this.customer = customer; }
    public Customer getCustomer() { return customer; }

    public void setProduct(InsuranceProduct product) { this.product = product; }
    public InsuranceProduct getProduct() { return product; }

    public void enterPersonalInfo(String name, String birthDate,
                                   String contact, String address) {
        System.out.println("  [시스템] 개인정보가 입력되었습니다.");
    }

    public void selectSpecialTerms(List<String> selectedSpecialTerms) {
        this.selectedSpecialTerms = selectedSpecialTerms;
    }

    public void selectPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean authenticate() {
        System.out.println("  [시스템] 본인인증이 완료되었습니다.");
        return true;
    }

    public void apply() {
        this.appliedAt = LocalDateTime.now();
    }

    public int getApplicationNumber() { return applicationNumber; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public String getPaymentMethod() { return paymentMethod; }
    public List<String> getSelectedSpecialTerms() { return selectedSpecialTerms; }
}
