package dp.dao;

import dp.consultation.PolicyApplication;
import dp.db.DBA;
import java.util.List;

public class PolicyApplicationDAO {

    public static void save(PolicyApplication p) {
        String customerId = p.getCustomer() != null ? p.getCustomer().getCustomerId() : null;
        DBA.executeUpdate(
            "INSERT INTO policy_applications (application_no, customer_id, customer_name,"
            + " product_name, period, payment_method, submitted_at, status)"
            + " VALUES (?,?,?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), status=VALUES(status)",
            p.getApplicationNumber(),
            customerId,
            p.getCustomerName(),
            p.getProductName(),
            p.getPeriod(),
            p.getPaymentMethod(),
            p.getSubmittedAt(),
            p.getStatus() != null ? p.getStatus() : "신청");
    }

    public static List<PolicyApplication> findAll() {
        return DBA.executeQuery(
            "SELECT application_no, customer_id, customer_name, product_name,"
            + " period, payment_method FROM policy_applications"
            + " WHERE status='신청'",
            rs -> PolicyApplication.fromDb(
                rs.getInt("application_no"),
                rs.getString("customer_id"),
                rs.getString("customer_name"),
                rs.getString("product_name"),
                rs.getInt("period"),
                rs.getString("payment_method")));
    }
}
