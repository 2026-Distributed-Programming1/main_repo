package dp.claim;

import dp.actor.Customer;
import dp.enums.AccidentReportStatus;
import dp.enums.AccidentType;
import java.time.LocalDateTime;

/**
 * 사고 접수 (AccidentReport)
 *
 * 고객이 자동차 사고를 보험사에 접수할 때 생성되는 클래스이다.
 * 「사고를 접수한다」 유스케이스의 중심 클래스로, 고객이 사고 정보를 단계별로 입력하면
 * 시스템이 당사 가입 내역과 대조한 뒤 접수를 완료한다. 현장출동이 필요한 경우
 * requestDispatch() 메서드를 통해 Dispatch 객체를 생성하여 현장출동 서비스 부서로 전달한다.
 */
public class AccidentReport {

    private static int sequence = 0;             // 접수번호 자동 부여용

    private String reportNo;                     // 접수번호
    private Customer customer;                   // 사고자
    private String vehicleNo;                    // 차량번호
    private String ownerName;                    // 자동차 소유자명/피보험자명
    private String contact;                      // 휴대폰 번호
    private AccidentType accidentType;           // 사고 유형 - 사물/사람
    private String damageType;                   // 피해 유형
    private String location;                     // 사고 위치
    private boolean needsDispatch;               // 현장출동 필요 여부
    private boolean agreedTerms;                 // 위치기반 서비스 약관 동의
    private LocalDateTime reportedAt;            // 접수일시
    private AccidentReportStatus status;         // 상태

    /** 생성자 - 접수번호 자동 부여, 접수일시 = now() */
    public AccidentReport(Customer customer) {
        sequence += 1;
        this.reportNo = "ACC" + String.format("%05d", sequence);
        this.customer = customer;
        this.reportedAt = LocalDateTime.now();
        this.status = AccidentReportStatus.DRAFT;
        this.agreedTerms = false;
    }

    /** 차량 정보 입력 */
    public void enterVehicleInfo(String vehicleNo, String ownerName, String contact) {
        this.vehicleNo = vehicleNo;
        this.ownerName = ownerName;
        this.contact = contact;
    }

    /** 사고 유형 선택 */
    public void selectAccidentType(AccidentType type, String damage) {
        this.accidentType = type;
        this.damageType = damage;
    }

    /** 사고 위치 입력 - GPS 자동 또는 수동 */
    public void enterLocation(String location) {
        this.location = location;
    }

    /** 현장출동 옵션 선택 */
    public void setDispatchOption(boolean needs) {
        this.needsDispatch = needs;
    }

    /** 약관 동의 */
    public void agreeTerms() {
        this.agreedTerms = true;
    }

    /** 필수 항목 검증 */
    public boolean validateRequiredFields() {
        return vehicleNo != null && ownerName != null && contact != null
                && accidentType != null && location != null && agreedTerms;
    }

    /**
     * 당사 가입 내역 대조 (E1)
     * 외부 시스템 연동이 필요하므로 더미로 처리한다.
     */
    public boolean verifyContract() {
        return customer != null;
    }

    /** 접수 처리 - status 갱신 */
    public void receive() {
        if (validateRequiredFields() && verifyContract()) {
            this.status = AccidentReportStatus.RECEIVED;
            System.out.println("[AccidentReport] 사고 접수 완료: " + reportNo);
        }
    }

    /** 현장출동 신청 - Dispatch 객체 생성 */
    public Dispatch requestDispatch() {
        if (this.needsDispatch && this.status == AccidentReportStatus.RECEIVED) {
            return new Dispatch(this);
        }
        return null;
    }

    /** 접수만 진행/취소 (A2) */
    public void cancel() {
        this.status = AccidentReportStatus.CANCELED;
    }

    // Getter
    public String getReportNo() { return reportNo; }
    public Customer getCustomer() { return customer; }
    public String getVehicleNo() { return vehicleNo; }
    public String getOwnerName() { return ownerName; }
    public String getContact() { return contact; }
    public AccidentType getAccidentType() { return accidentType; }
    public String getDamageType() { return damageType; }
    public String getLocation() { return location; }
    public boolean isNeedsDispatch() { return needsDispatch; }
    public boolean isAgreedTerms() { return agreedTerms; }
    public LocalDateTime getReportedAt() { return reportedAt; }
    public AccidentReportStatus getStatus() { return status; }
}
