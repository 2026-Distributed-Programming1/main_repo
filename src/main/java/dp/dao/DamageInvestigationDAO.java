package dp.dao;

import dp.actor.Customer;
import dp.claim.ClaimRequest;
import dp.claim.DamageInvestigation;
import dp.contract.Contract;
import dp.db.DBA;
import dp.enums.ClaimRequestStatus;
import dp.enums.InvestigationStatus;
import java.util.List;

public class DamageInvestigationDAO {

    public static void save(DamageInvestigation inv) {
        String claimNo  = inv.getClaim() != null ? inv.getClaim().getClaimNo() : null;
        String claimCus = inv.getClaim() != null && inv.getClaim().getCustomer() != null
                ? inv.getClaim().getCustomer().getName() : null;
        String customerId = inv.getClaim() != null && inv.getClaim().getCustomer() != null
                ? inv.getClaim().getCustomer().getCustomerId() : null;
        String handlerId = inv.getHandler() != null ? inv.getHandler().getEmployeeId() : null;
        String handlerName = inv.getHandler() != null ? inv.getHandler().getName() : null;
        String result    = inv.getResult() != null ? inv.getResult().name() : null;
        String status    = inv.getStatus() != null ? inv.getStatus().name() : null;
        DBA.executeUpdate(
            "INSERT INTO damage_investigations (investigation_no, claim_no, claim_customer,"
            + " customer_id, handler_emp_id, handler_name, our_fault_ratio, counter_ratio,"
            + " recognized_damage, opinion, result, reject_reason, investigated_at, status)"
            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE status=VALUES(status), result=VALUES(result),"
            + " recognized_damage=VALUES(recognized_damage), investigated_at=VALUES(investigated_at)",
            inv.getInvestigationNo(), claimNo, claimCus,
            customerId, handlerId, handlerName,
            inv.getOurFaultRatio(), inv.getCounterFaultRatio(),
            inv.getRecognizedDamage(), inv.getOpinion(),
            result, inv.getRejectReason(), inv.getInvestigatedAt(), status);
    }

    public static List<DamageInvestigation> findAll() {
        return DBA.executeQuery(
            "SELECT investigation_no, claim_no, claim_customer, customer_id, handler_name,"
            + " our_fault_ratio, counter_ratio, recognized_damage, opinion,"
            + " result, status FROM damage_investigations",
            rs -> {
                String cn    = rs.getString("claim_no");
                String cname = rs.getString("claim_customer");
                String cid   = rs.getString("customer_id");
                Customer custShell = new Customer(
                    cid != null ? cid : "?", cname != null ? cname : "", null, null, null);
                ClaimRequest claimShell = new ClaimRequest(
                    cn != null ? cn : "?", custShell, null, ClaimRequestStatus.RECEIVED);
                String st = rs.getString("status");
                InvestigationStatus status = InvestigationStatus.NEW_ASSIGNED;
                if (st != null) {
                    try { status = InvestigationStatus.valueOf(st); }
                    catch (IllegalArgumentException ignored) {}
                }
                return new DamageInvestigation(
                    rs.getString("investigation_no"),
                    claimShell,
                    rs.getString("handler_name"),
                    rs.getDouble("our_fault_ratio"),
                    rs.getDouble("counter_ratio"),
                    rs.getLong("recognized_damage"),
                    status);
            });
    }
}