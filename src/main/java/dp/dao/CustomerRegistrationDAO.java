package dp.dao;

import dp.db.DBA;
import dp.enums.InsuranceType;
import dp.sales.CustomerRegistration;
import java.util.List;

public class CustomerRegistrationDAO {

    public static void save(CustomerRegistration r) {
        String insuranceType = r.getInsuranceType() != null ? r.getInsuranceType().name() : null;
        DBA.executeUpdate(
            "INSERT INTO customer_registrations (customer_id, name, ssn_masked, phone,"
            + " insurance_type, contract_date, expiry_date, monthly_premium)"
            + " VALUES (?,?,?,?,?,?,?,?)",
            r.getCustomerId(),
            r.getName(),
            r.getMaskedSsn(),
            r.getPhone(),
            insuranceType,
            r.getContractDate(),
            r.getExpiryDate(),
            r.getMonthlyPremium());
    }

    public static List<CustomerRegistration> findAll() {
        return DBA.executeQuery(
            "SELECT customer_id, name, ssn_masked, phone, insurance_type,"
            + " contract_date, expiry_date, monthly_premium FROM customer_registrations",
            rs -> {
                String it = rs.getString("insurance_type");
                InsuranceType insuranceType = null;
                if (it != null) {
                    try { insuranceType = InsuranceType.valueOf(it); }
                    catch (IllegalArgumentException ignored) {}
                }
                java.sql.Date cd = rs.getDate("contract_date");
                java.sql.Date ed = rs.getDate("expiry_date");
                return new CustomerRegistration(
                        rs.getString("customer_id"),
                        null,
                        rs.getString("name"),
                        rs.getString("ssn_masked"),
                        rs.getString("phone"),
                        insuranceType,
                        cd != null ? cd.toLocalDate() : null,
                        ed != null ? ed.toLocalDate() : null,
                        rs.getLong("monthly_premium"));
            });
    }
}