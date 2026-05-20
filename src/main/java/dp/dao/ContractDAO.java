package dp.dao;

import dp.actor.Customer;
import dp.contract.Contract;
import dp.db.DBA;
import dp.enums.ContractStatus;
import java.time.LocalDate;
import java.util.List;

public class ContractDAO {

    public static void save(Contract c) {
        String customerId   = c.getCustomer() != null ? c.getCustomer().getCustomerId() : null;
        String customerName = c.getCustomer() != null ? c.getCustomer().getName() : null;
        String status       = c.getStatus() != null ? c.getStatus().name() : "NORMAL";
        DBA.executeUpdate(
            "INSERT INTO contracts (contract_no, policy_no, customer_id, customer_name,"
            + " contract_date, expiry_date, monthly_premium, insurance_type, status,"
            + " is_expiring_soon, is_overdue, overdue_count)"
            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE status=VALUES(status), is_expiring_soon=VALUES(is_expiring_soon),"
            + " is_overdue=VALUES(is_overdue), overdue_count=VALUES(overdue_count),"
            + " monthly_premium=VALUES(monthly_premium)",
            c.getContractNo(), c.getPolicyNo(), customerId, customerName,
            c.getContractDate(), c.getExpiryDate(), c.getMonthlyPremium(),
            c.getInsuranceType(), status,
            c.getIsExpiringSoon(), c.getIsOverdue(),
            c.getOverdueCount() != null ? c.getOverdueCount() : 0);
    }

    public static List<Contract> findAll() {
        return DBA.executeQuery(
            "SELECT contract_no, policy_no, customer_id, customer_name,"
            + " contract_date, expiry_date, monthly_premium, insurance_type, status,"
            + " is_expiring_soon, is_overdue, overdue_count FROM contracts",
            rs -> mapRow(rs));
    }

    public static List<Contract> findByCustomerId(String customerId) {
        return DBA.executeQuery(
            "SELECT contract_no, policy_no, customer_id, customer_name,"
            + " contract_date, expiry_date, monthly_premium, insurance_type, status,"
            + " is_expiring_soon, is_overdue, overdue_count FROM contracts WHERE customer_id=?",
            rs -> mapRow(rs), customerId);
    }

    public static Contract findByContractNo(String contractNo) {
        return DBA.queryOne(
            "SELECT contract_no, policy_no, customer_id, customer_name,"
            + " contract_date, expiry_date, monthly_premium, insurance_type, status,"
            + " is_expiring_soon, is_overdue, overdue_count FROM contracts WHERE contract_no=?",
            rs -> mapRow(rs), contractNo);
    }

    private static Contract mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        String st = rs.getString("status");
        ContractStatus status = ContractStatus.NORMAL;
        if (st != null) {
            try { status = ContractStatus.valueOf(st); }
            catch (IllegalArgumentException ignored) {}
        }
        String cid  = rs.getString("customer_id");
        String name = rs.getString("customer_name");
        Customer customer = cid != null
                ? new Customer(cid, name != null ? name : "", null, null, null)
                : null;
        return new Contract(
                rs.getString("contract_no"),
                rs.getString("policy_no"),
                customer,
                toLocalDate(rs.getDate("contract_date")),
                toLocalDate(rs.getDate("expiry_date")),
                rs.getLong("monthly_premium"),
                rs.getString("insurance_type"),
                status,
                rs.getBoolean("is_expiring_soon"),
                rs.getBoolean("is_overdue"),
                rs.getInt("overdue_count"));
    }

    private static LocalDate toLocalDate(java.sql.Date d) {
        return d != null ? d.toLocalDate() : null;
    }
}