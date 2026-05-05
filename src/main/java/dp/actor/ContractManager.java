package dp.actor;

/**
 * 계약관리 담당자 (ContractManager)
 */
public class ContractManager {

    private String managerId;
    private String name;
    private String department;

    // 기본 생성자
    public ContractManager(String managerId, String name, String department) {
        this.managerId = managerId;
        this.name = name;
        this.department = department;
    }

    // Getter
    public String getManagerId() { return managerId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
}