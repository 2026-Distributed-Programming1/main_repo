package dp.consultation;

import dp.actor.Customer;
import java.time.LocalDateTime;

/**
 * 청약서 (PolicyApplication)
 * UC: 청약서를 작성한다
 */
public class PolicyApplication {

    private static int sequence = 0;

    private int applicationNumber;
    private LocalDateTime submittedAt;
    private Customer customer;
    private String customerName;
    private String productName;
    private int period;
    private String paymentMethod;

    public PolicyApplication() {
        sequence += 1;
        this.applicationNumber = sequence;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerName = customer.getName();
    }

    public Customer getCustomer() { return customer; }

    public void enterCustomerInfo(String customerName, String birthDate,
                                   String contact, String address) {
        this.customerName = customerName;
    }

    public void selectProduct(String productName, int period, String paymentMethod) {
        this.productName = productName;
        this.period = period;
        this.paymentMethod = paymentMethod;
    }

    public void attachSignature(String file) {
        System.out.println("  [시스템] 서명 파일이 첨부되었습니다: " + file);
    }

    public void requestElectronicSignature() {
        System.out.println("  [시스템] 고객에게 전자서명 요청이 발송되었습니다.");
    }

    public void submit() {
        this.submittedAt = LocalDateTime.now();
    }

    public int getApplicationNumber() { return applicationNumber; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public String getCustomerName() { return customerName; }
    public String getProductName() { return productName; }
    public int getPeriod() { return period; }
    public String getPaymentMethod() { return paymentMethod; }
}
