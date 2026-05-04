package dp.runner.usecase;

import dp.actor.InsuranceReviewer;
import dp.consultation.PolicyApplication;
import dp.consultation.ReviewResult;
import dp.consultation.Underwriting;
import dp.runner.ConsoleHelper;
import dp.runner.Repository;

/**
 * UC: 인수 심사를 한다 / 심사 결과를 전달한다 시나리오 진행자
 *
 * [인수 심사를 한다] Basic Path:
 *   1. 보험 심사자는 [인수 심사] 메뉴를 클릭한다.
 *   2. 시스템은 인수 심사 화면을 출력한다. (E1)
 *   3. 보험 심사자는 심사 대상 목록에서 심사할 신청 건을 클릭한다.
 *   4. 시스템은 신청 상세 정보를 출력한다.
 *   5. 보험 심사자는 고객 정보 및 계약 조건을 검토하고 [심사 시작] 버튼을 클릭한다.
 *   6. 시스템은 자동 심사 결과를 출력한다. (A1)
 *   7. 보험 심사자는 최종 심사 결과(승인/거절/조건부승인)를 선택한다.
 *   8. 시스템은 심사 결과 입력 화면을 출력한다.
 *   9. 보험 심사자는 심사 결과 내용을 입력하고 [심사 완료] 버튼을 클릭한다.
 *  10. 심사 결과를 전달한다 유스케이스 1번으로 이동한다.
 *  11. 심사 결과를 전달한다 유스케이스 8번에서 넘어온다.
 *  12~14. 시스템은 심사 완료 결과를 출력한다.
 *
 * Alternative:
 *   A1) 자동 심사로 처리가 불가한 경우 → 수동 심사 화면 출력
 *       (심사 유형 선택, 필요 서류 첨부, 심사 의견 입력)
 *
 * Exception:
 *   E1) 심사 대기 건이 없는 경우 → 안내 메시지 출력
 *   E2) 심사 결과 저장 실패 시 → 재시도 안내
 *
 * [심사 결과를 전달한다] Basic Path:
 *   1. 인수 심사를 한다 유스케이스 10번에서 넘어온다.
 *   2. 시스템은 심사 결과 전달 화면을 출력하고 판매채널에게 알림을 자동 발송한다.
 *   3. 판매채널은 알림을 확인하고 [결과 확인] 버튼을 클릭한다.
 *   4. 시스템은 심사 결과 상세 정보를 출력한다. (A1)
 *   5. 판매채널은 [확인 완료] 버튼을 클릭한다.
 *   6. 시스템은 확인 완료 처리 결과를 출력한다.
 *   7. 판매채널은 [닫기] 버튼을 클릭한다.
 *   8. 시스템은 인수 심사를 한다 유스케이스 11번으로 이동한다.
 *
 * Alternative (심사 결과를 전달한다):
 *   A1) 심사 결과가 조건부 승인인 경우 → 조건부 승인 조건 출력, 고객 안내
 */
public class UnderwritingRunner {

    public static void run() {
        ConsoleHelper.printDoubleDivider();
        System.out.println("UC: 인수 심사를 한다");
        ConsoleHelper.printDoubleDivider();

        // 2. 시스템은 인수 심사 화면을 출력한다. (E1)
        if (Repository.policyApplications.isEmpty() && Repository.insuranceApplications.isEmpty()) {
            // E1) 심사 대기 건이 없는 경우
            ConsoleHelper.printError("[E1] 처리할 심사 건이 없습니다.");
            ConsoleHelper.waitEnter();
            return;
        }

        InsuranceReviewer reviewerActor = Repository.insuranceReviewers.get(0);
        PolicyApplication application = Repository.policyApplications.isEmpty()
                ? new PolicyApplication()
                : Repository.policyApplications.get(Repository.policyApplications.size() - 1);

        ConsoleHelper.printStage("시스템", "심사 대기 목록을 출력합니다.");
        ConsoleHelper.printInfo("신청번호: " + application.getApplicationNumber()
                + " | 고객명: " + (application.getCustomerName() != null ? application.getCustomerName() : "미정"));

        // 3~4. 보험 심사자는 심사 건을 선택한다.
        ConsoleHelper.printStage("보험심사자", "신청 상세 정보를 확인합니다.");
        ConsoleHelper.printInfo("상품명: " + application.getProductName()
                + " | 보험기간: " + application.getPeriod() + "년"
                + " | 납입방법: " + application.getPaymentMethod());

        // 5. 보험 심사자는 [심사 시작] 버튼을 클릭한다.
        Underwriting underwriting = reviewerActor.startUnderwriting(application);

        // 6. 시스템은 자동 심사 결과를 출력한다. (A1)
        int reviewMethod = ConsoleHelper.readMenuChoice(
                "[시스템] 심사 방법을 선택하세요.",
                "자동심사", "수동심사 (A1)");

        if (reviewMethod == 1) {
            underwriting.autoReview();
            ConsoleHelper.printStage("시스템", "자동 심사 결과: 위험등급 일반, 인수 가능");
        } else {
            // A1) 자동 심사로 처리가 불가한 경우 → 수동 심사
            ConsoleHelper.printStage("시스템", "[A1] 수동 심사가 필요합니다.");
            int typeChoice = ConsoleHelper.readMenuChoice(
                    "[보험심사자] 심사 유형을 선택하세요.",
                    "진단심사", "특인심사", "일반심사", "이미지심사");
            String reviewType = switch (typeChoice) {
                case 1 -> "진단심사";
                case 2 -> "특인심사";
                case 3 -> "일반심사";
                case 4 -> "이미지심사";
                default -> "일반심사";
            };

            ConsoleHelper.printStage("시스템", "심사 유형: " + reviewType + " | 필요 서류 목록을 출력합니다.");
            boolean attach = ConsoleHelper.readYesNo("[보험심사자] 필요 서류를 첨부하시겠습니까?");
            if (attach) {
                underwriting.attachDocument("document.pdf");
            }
            String opinion = ConsoleHelper.readNonEmpty("  심사 의견: ");
            underwriting.manualReview(reviewType, opinion);
        }

        // 7~9. 보험 심사자는 최종 심사 결과를 입력하고 [심사 완료] 버튼을 클릭한다.
        ConsoleHelper.printStage("시스템", "심사 결과 입력 화면을 출력합니다.");
        int resultChoice = ConsoleHelper.readMenuChoice(
                "[보험심사자] 최종 심사 결과를 선택하세요.",
                "승인", "조건부승인", "거절");
        String resultStr = switch (resultChoice) {
            case 1 -> "승인";
            case 2 -> "조건부승인";
            case 3 -> "거절";
            default -> "승인";
        };
        String condition = null;
        String rejectionReason = null;
        if (resultChoice == 2) {
            condition = ConsoleHelper.readNonEmpty("  조건부승인 조건: ");
        } else if (resultChoice == 3) {
            rejectionReason = ConsoleHelper.readNonEmpty("  거절 사유: ");
        }
        underwriting.complete(resultStr, condition, rejectionReason);
        Repository.underwritings.add(underwriting);

        // 10. 심사 결과를 전달한다 유스케이스로 이동
        ConsoleHelper.printDoubleDivider();
        System.out.println("UC: 심사 결과를 전달한다");
        ConsoleHelper.printDoubleDivider();

        ReviewResult result = underwriting.getReviewResult();

        // 2. 시스템은 심사 결과 전달 화면을 출력하고 판매채널에게 알림을 자동 발송한다.
        reviewerActor.deliverReviewResult(result);
        ConsoleHelper.printStage("시스템", "판매채널에게 심사 결과 알림을 자동 발송합니다.");
        ConsoleHelper.printInfo("심사결과: " + result.getResult());

        // 3~4. 판매채널은 알림을 확인하고 결과 상세 정보를 조회한다.
        ConsoleHelper.printStage("판매채널", "수신한 알림을 확인하고 [결과 확인] 버튼을 클릭합니다.");
        ConsoleHelper.printStage("시스템", "심사 결과 상세 정보를 출력합니다.");
        ConsoleHelper.printInfo("심사결과: " + result.getResult());
        if (result.getCondition() != null) {
            // A1) 조건부 승인인 경우
            ConsoleHelper.printInfo("[A1] 조건부승인 조건: " + result.getCondition());
            ConsoleHelper.printInfo("판매채널은 고객에게 조건부 승인 내용을 안내합니다.");
        }
        if (result.getRejectionReason() != null) {
            ConsoleHelper.printInfo("거절사유: " + result.getRejectionReason());
        }

        // 5~6. 판매채널은 [확인 완료] 버튼을 클릭한다.
        boolean confirm = ConsoleHelper.readYesNo("[판매채널] 확인 완료 처리하시겠습니까?");
        if (confirm) {
            result.confirm();
            ConsoleHelper.printStage("시스템", "확인 완료 처리 결과를 출력합니다.");
            ConsoleHelper.printInfo("확인일시: " + result.getConfirmedAt());
        }

        // 8. 시스템은 인수 심사를 한다 유스케이스 11번으로 이동한다.
        // 11~14. 심사 완료 결과 출력
        ConsoleHelper.printStage("시스템", "\"심사가 완료되었습니다.\" 메시지를 출력합니다.");
        ConsoleHelper.printStage("보험심사자", "[확인] 버튼을 클릭합니다.");
        ConsoleHelper.printStage("시스템", "심사 완료 결과를 출력합니다.");
        ConsoleHelper.printInfo("심사번호: " + underwriting.getReviewNumber()
                + " | 심사일시: " + underwriting.getReviewedAt()
                + " | 처리결과: " + result.getResult());

        ConsoleHelper.waitEnter();
    }
}
