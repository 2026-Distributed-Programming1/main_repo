package dp.actor;
/** 수정 필요 */
import java.time.LocalDate;

/**
 * 사원 (Employee) - 추상 클래스
 *
 * 보험사의 모든 임직원의 공통 부모 클래스.
 */
public abstract class Employee extends User {

    private static int sequence = 0;    // 사원 ID 자동 부여용

    protected String employeeId;       // 사원 ID
    protected String department;       // 부서
    protected String position;         // 직책
    protected LocalDate hireDate;      // 입사일

    /** 생성자 */
    public Employee(String name, String dept, String position) {
        super(name, null, null);
        sequence += 1;
        this.employeeId = "EMP" + String.format("%05d", sequence);
        this.department = dept;
        this.position = position;
        this.hireDate = LocalDate.now();
    }

    // Getter
    public String getEmployeeId() { return employeeId; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
    public LocalDate getHireDate() { return hireDate; }
}
