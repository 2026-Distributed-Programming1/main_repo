package dp.dao;

import dp.claim.ClaimCalculation;
import dp.claim.ClaimPayment;
import dp.db.DBA;
import dp.enums.ClaimPaymentStatus;
import java.util.List;

public class ClaimPaymentDAO {

    public static void save(ClaimPayment p) {
        String calcNo = p.getCalculation() != null ? p.getCalculation().getCalculationNo() : null;
        String status = p.getStatus() != null ? p.getStatus().name() : null;
        String paymentType = p.getPaymentType() != null ? p.getPaymentType().name() : null;
        String recipientName = p.getRecipient() != null ? p.getRecipient().getName() : null;
        String accountNo = p.getAccount() != null ? p.getAccount().getAccountNo() : null;
        DBA.executeUpdate(
            "INSERT INTO claim_payments (payment_no, calculation_no, final_amount,"
            + " paid_at, scheduled_at, payment_type, recipient_name, account_no, failure_reason, status)"
            + " VALUES (?,?,?,?,?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE status=VALUES(status),"
            + " paid_at=VALUES(paid_at), failure_reason=VALUES(failure_reason)",
            p.getPaymentNo(), calcNo, p.getFinalAmount(),
            p.getPaidAt(), p.getScheduledAt(), paymentType,
            recipientName, accountNo, p.getFailureReason(), status);
    }

    public static List<ClaimPayment> findAll() {
        return DBA.executeQuery(
            "SELECT payment_no, calculation_no, final_amount,"
            + " paid_at, scheduled_at, payment_type, recipient_name, account_no, status FROM claim_payments",
            rs -> {
                String cno = rs.getString("calculation_no");
                ClaimCalculation calcShell = new ClaimCalculation(
                    cno != null ? cno : "?", null, 0, 0, 0,
                    false, false, null);
                String st = rs.getString("status");
                ClaimPaymentStatus status = ClaimPaymentStatus.WAITING;
                if (st != null) {
                    try { status = ClaimPaymentStatus.valueOf(st); }
                    catch (IllegalArgumentException ignored) {}
                }
                return new ClaimPayment(
                    rs.getString("payment_no"), calcShell,
                    rs.getLong("final_amount"), status);
            });
    }
}