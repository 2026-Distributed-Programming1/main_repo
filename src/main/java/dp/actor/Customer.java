package dp.actor;


import dp.common.BankAccount;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 고객 (Customer)
 *
 * 시스템에 등록된 고객을 표현하는 클래스로, User를 상속한다.
 * 보험 가입, 보험금 청구, 사고 접수, 문의 등 다양한 유스케이스의 시작점이 된다.
 */
public class Customer extends User {

    private static int sequence = 0;    // 고객번호 자동 부여용

    private String customerNo;                       // 고객번호
    private String residentNo;                       // 주민등록번호
    private String address;                          // 주소
    private LocalDate birthDate;                     // 생년월일
    private List<BankAccount> registeredAccounts;    // 등록된 계좌 목록
    private LocalDateTime registeredAt;              // 가입일시

    /** 생성자 - 고객번호 자동 부여 */
    public Customer(String name, String residentNo, String contact, String email) {
        super(name, contact, email);
        sequence += 1;
        this.customerNo = "CUS" + String.format("%05d", sequence);
        this.residentNo = residentNo;
        this.registeredAccounts = new ArrayList<>();
        this.registeredAt = LocalDateTime.now();
    }

    /** 주소 입력 */
    public void enterAddress(String address) {
        this.address = address;
    }

    /** 생년월일 입력 */
    public void enterBirthDate(LocalDate date) {
        this.birthDate = date;
    }

    /** 계좌 등록 */
    public void registerAccount(BankAccount account) {
        this.registeredAccounts.add(account);
    }

    // ====== 유스케이스 진입점 ======
    // 시나리오 추적성을 위해 정의된 메서드들. 실제 객체 생성과 협력은
    // 호출 측(Main 또는 외부)에서 처리하지만, 시작점을 명시한다.
    //
    // 7️⃣·8️⃣ 도메인과 직접 관련된 메서드만 본 구현에 포함하고,
    // 다른 도메인(상담, 청약, 문의 등)의 진입점은 해당 도메인 구현 시 추가한다.

    // Getter
    public String getCustomerNo() { return customerNo; }
    public String getResidentNo() { return residentNo; }
    public String getAddress() { return address; }
    public LocalDate getBirthDate() { return birthDate; }
    public List<BankAccount> getRegisteredAccounts() { return registeredAccounts; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
}
