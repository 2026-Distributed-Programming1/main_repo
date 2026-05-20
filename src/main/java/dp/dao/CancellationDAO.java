package dp.dao;

import dp.actor.Customer;
import dp.contract.Cancellation;
import dp.contract.Contract;
import dp.db.DBA;
import java.util.List;

public class CancellationDAO {

    public static void save(Cancellation c) {
        String contractNo   = c.getContract() != null ? c.getContract().getContractNo() : null;
        String customerName = c.getContract() != null && c.getContract().getCustomer() != null
                ? c.getContract().getCustomer().getName() : null;
        long monthlyPremium = c.getContract() != null ? c.getContract().getMonthlyPremium() : 0L;
        DBA.executeUpdate(
            "INSERT INTO cancellations (cancellation_no, contract_no, customer_name,"
            + " monthly_premium, reason, expected_refund, status, cancelled_at)"
            + " VALUES (?,?,?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE status=VALUES(status), cancelled_at=VALUES(cancelled_at)",
            c.getCancellationNo(), contractNo, customerName,
            monthlyPremium, c.getReason(), c.getExpectedRefund(),
            c.getStatus(), c.getCanceledAt());
    }

    public static List<Cancellation> findAll() {
        return DBA.executeQuery(
            "SELECT cancellation_no, contract_no, customer_name, monthly_premium,"
            + " reason, expected_refund, status FROM cancellations",
            rs -> {
                String cno   = rs.getString("contract_no");
                String cname = rs.getString("customer_name");
                long premium = rs.getLong("monthly_premium");
                Customer custShell = new Customer(
                    cno != null ? cno : "?", cname != null ? cname : "", null, null, null);
                Contract contractShell = Contract.shellOf(cno, custShell, premium);
                return new Cancellation(
                    rs.getString("cancellation_no"),
                    contractShell,
                    rs.getString("reason"),
                    rs.getLong("expected_refund"),
                    rs.getString("status"));
            });
    }
}