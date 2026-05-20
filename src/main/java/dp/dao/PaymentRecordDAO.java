package dp.dao;

import dp.contract.Contract;
import dp.db.DBA;
import dp.enums.PaymentRecordStatus;
import dp.payment.PaymentRecord;
import java.time.LocalDate;
import java.util.List;

public class PaymentRecordDAO {

    public static void save(PaymentRecord r) {
        String contractNo   = r.getContract() != null ? r.getContract().getContractNo() : null;
        String customerName = r.getContract() != null && r.getContract().getCustomer() != null
                ? r.getContract().getCustomer().getName() : null;
        String status       = r.getStatus() != null ? r.getStatus().name() : null;
        DBA.executeUpdate(
            "INSERT INTO payment_records (record_no, contract_no, customer_name, amount,"
            + " method, payment_date, status)"
            + " VALUES (?,?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE status=VALUES(status)",
            r.getRecordNo(), contractNo, customerName,
            r.getAmount(), r.getMethod(), r.getPaymentDate(), status);
    }

    public static List<PaymentRecord> findAll() {
        return DBA.executeQuery(
            "SELECT record_no, contract_no, customer_name, amount, method, payment_date, status"
            + " FROM payment_records",
            rs -> {
                String cno = rs.getString("contract_no");
                Contract contractShell = Contract.shellOf(cno, null, 0L);
                String st = rs.getString("status");
                PaymentRecordStatus status = PaymentRecordStatus.WAITING;
                if (st != null) {
                    try { status = PaymentRecordStatus.valueOf(st); }
                    catch (IllegalArgumentException ignored) {}
                }
                java.sql.Date pd = rs.getDate("payment_date");
                LocalDate paymentDate = pd != null ? pd.toLocalDate() : LocalDate.now();
                return new PaymentRecord(
                    rs.getString("record_no"), contractShell,
                    rs.getLong("amount"), rs.getString("method"),
                    paymentDate, status);
            });
    }

    public static List<PaymentRecord> findByContractNo(String contractNo) {
        return DBA.executeQuery(
            "SELECT record_no, contract_no, customer_name, amount, method, payment_date, status"
            + " FROM payment_records WHERE contract_no=?",
            rs -> {
                Contract contractShell = new Contract();
                contractShell.setContractNo(rs.getString("contract_no"));
                String st = rs.getString("status");
                PaymentRecordStatus status = PaymentRecordStatus.WAITING;
                if (st != null) {
                    try { status = PaymentRecordStatus.valueOf(st); }
                    catch (IllegalArgumentException ignored) {}
                }
                java.sql.Date pd = rs.getDate("payment_date");
                LocalDate paymentDate = pd != null ? pd.toLocalDate() : LocalDate.now();
                return new PaymentRecord(
                    rs.getString("record_no"), contractShell,
                    rs.getLong("amount"), rs.getString("method"),
                    paymentDate, status);
            }, contractNo);
    }
}