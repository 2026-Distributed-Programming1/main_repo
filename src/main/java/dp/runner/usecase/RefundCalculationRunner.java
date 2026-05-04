package com.insurance.runner.usecase;

import com.insurance.contract.Cancellation;
import com.insurance.contract.InsuranceContract;
import com.insurance.enums.RefundStatus;
import com.insurance.payment.RefundCalculation;
import com.insurance.payment.RefundPayment;
import com.insurance.runner.ConsoleHelper;
import com.insurance.runner.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UC9: 「해약 환급금을 산출한다」 시나리오 진행자
 *
 * 정상 흐름:
 * 1. 해지 건 선택 → 자동 산출 → 검토 → 확정 → 지급 이관
 * 분기:
 * - A1: 공제 항목 수기 조정 → 재산출
 * - A2: 산출 내역서 PDF 다운로드
 * - A3: 목록으로 돌아가기
 * - E1: 필수 데이터 누락
 * - E2: 확정 저장 오류
 */
public class RefundCalculationRunner {

    public static void run() {
        ConsoleHelper.printDoubleDivider();
        System.out.println("UC9: 해약 환급금을 산출한다");
        ConsoleHelper.printDoubleDivider();

        // 1) 해지 건 선택 또는 새 해지 생성
        Cancellation cancellation = selectOrCreateCancellation();
        if (cancellation == null) return;

        // 2) RefundCalculation 생성 (생성 시 자동 산출)
        RefundCalculation refund = new RefundCalculation(cancellation);
        Repository.refundCalculations.add(refund);

        // E1: 필수 데이터 누락 검증
        if (refund.getStatus() == RefundStatus.CALCULATION_PENDING) {
            ConsoleHelper.printError("[E1] 환급금 산출에 필요한 데이터가 누락되었습니다.");
            ConsoleHelper.waitEnter();
            return;
        }

        // 3) 산출 결과 검토 및 처리 루프
        while (true) {
            showRefund(refund);
            int choice = ConsoleHelper.readMenuChoice(
                    "[재무회계 담당자] 작업을 선택하세요:",
                    "환급금 확정 → 지급 이관 (정상)",
                    "공제 항목 수기 조정 (A1)",
                    "산출 내역서 PDF 다운로드 (A2)",
                    "목록으로 돌아가기 (A3)");
            switch (choice) {
                case 1:
                    confirmAndProceed(refund);
                    return;
                case 2:
                    adjustDeduction(refund);
                    break;
                case 3:
                    refund.exportPDF();
                    break;
                case 4:
                    refund.goBackToList();
                    return;
            }
        }
    }

    private static void confirmAndProceed(RefundCalculation refund) {
        if (!ConsoleHelper.readYesNo("환급금 " + refund.getFinalRefund()
                + "원으로 확정 후 지급 단계로 이관하시겠습니까?")) {
            return;
        }
        RefundPayment payment = refund.confirm();
        if (payment != null) {
            Repository.refundPayments.add(payment);
            ConsoleHelper.printSuccess("환급금 지급 이관 완료: " + payment.getPaymentNo());
        } else {
            ConsoleHelper.printError("[E2] 확정 저장에 실패했습니다.");
        }
        ConsoleHelper.waitEnter();
    }

    /** A1: 공제 항목 수기 조정 */
    private static void adjustDeduction(RefundCalculation refund) {
        ConsoleHelper.printStage("재무회계 담당자", "[A1] 공제 항목을 수기로 조정합니다.");
        String item = ConsoleHelper.readNonEmpty("  공제 항목명 (예: 미납 보험료): ");
        long amount = ConsoleHelper.readLong("  조정 금액 (원): ");
        String note = ConsoleHelper.readNonEmpty("  조정 메모: ");
        refund.adjustDeduction(item, amount, note);
        refund.recalculate();
        ConsoleHelper.printSuccess("재산출 완료. 새 환급금: " + refund.getFinalRefund() + "원");
        ConsoleHelper.waitEnter();
    }

    private static void showRefund(RefundCalculation refund) {
        ConsoleHelper.printDivider();
        System.out.println("  환급 접수번호: " + refund.getRefundNo());
        System.out.println("  대상 해지: " + refund.getCancellation().getCancellationNo());
        System.out.println("  총 납입 보험료: " + refund.getTotalPaidPremium() + "원");
        System.out.println("  납입 기간: " + refund.getPaymentPeriod());
        System.out.println("  책임준비금: " + refund.getReserveAmount() + "원");
        System.out.println("  적용 이율: " + (refund.getAppliedRate() * 100) + "%");
        System.out.println("  기본 환급금: " + refund.getBaseRefund() + "원");
        System.out.println("  미납 보험료: " + refund.getUnpaidPremium() + "원");
        System.out.println("  대출 원금/이자: " + refund.getLoanPrincipal() + " / " + refund.getLoanInterest() + "원");
        System.out.println("  실지급 환급금: " + refund.getFinalRefund() + "원");
        System.out.println("  상태: " + refund.getStatus());
        ConsoleHelper.printDivider();
    }

    /** 진행 중 RefundCalculation이 없는 해지 건이 있으면 그것을, 없으면 신규 생성 */
    private static Cancellation selectOrCreateCancellation() {
        // 환급 산출이 안 된 해지 건들
        List<Cancellation> pending = Repository.cancellations.stream()
                .filter(c -> Repository.refundCalculations.stream().noneMatch(r -> r.getCancellation() == c))
                .collect(Collectors.toList());

        if (!pending.isEmpty()) {
            String[] options = pending.stream()
                    .map(c -> c.getCancellationNo() + " (계약 " + c.getContract().getContractNo() + ")")
                    .toArray(String[]::new);
            int choice = ConsoleHelper.readMenuChoice("[시스템] 환급금을 산출할 해지 건을 선택하세요:", options);
            return pending.get(choice - 1);
        }

        // 해지 건이 없으면 시연용으로 새 해지 생성
        ConsoleHelper.printInfo("산출 대상 해지 건이 없습니다. 시연용 해지 건을 생성합니다.");
        if (Repository.contracts.isEmpty()) {
            ConsoleHelper.printError("등록된 계약이 없습니다.");
            ConsoleHelper.waitEnter();
            return null;
        }
        String[] options = Repository.contracts.stream()
                .map(c -> c.getContractNo() + " - " + c.getCustomer().getName()
                        + " (월 " + c.getMonthlyPremium() + "원)")
                .toArray(String[]::new);
        int choice = ConsoleHelper.readMenuChoice("[시연용] 해지할 계약을 선택하세요:", options);
        InsuranceContract contract = Repository.contracts.get(choice - 1);
        Cancellation cancellation = new Cancellation(contract);
        cancellation.calculateExpectedRefund();
        cancellation.confirm();
        Repository.cancellations.add(cancellation);
        ConsoleHelper.printSuccess("시연용 해지 건 생성: " + cancellation.getCancellationNo());
        return cancellation;
    }
}
