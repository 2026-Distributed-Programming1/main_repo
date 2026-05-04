package dp.runner.usecase;

import dp.actor.EducationTrainer;
import dp.actor.SalesManager;
import dp.education.EducationPlan;
import dp.runner.ConsoleHelper;
import dp.runner.Repository;
import java.time.LocalDate;

/**
 * UC: 교육 계획안을 작성한다 시나리오 진행자
 *
 * Basic Path:
 *   1. 영업교육 담당자는 [교육 계획 작성] 항목을 클릭한다.
 *   2. 시스템은 교육 계획 작성 화면을 출력한다.
 *   3. 영업교육 담당자는 교육 계획 기본 정보 및 교육 내용을 입력한다.
 *   4. 시스템은 필수 항목 누락 및 형식 오류 여부를 실시간으로 검증한다. (E1)
 *   5. 영업교육 담당자는 [승인 요청] 버튼을 클릭한다. (A1, A2)
 *   6. 시스템은 승인 요청 확인 팝업을 출력한다.
 *   7. 영업교육 담당자는 [확인] 버튼을 클릭한다.
 *   8. 시스템은 영업 관리자에게 승인 요청 알림을 자동 발송한다.
 *   9. 영업 관리자는 [승인] 버튼을 클릭한다. (A3)
 *  10. 시스템은 승인 완료 결과를 출력한다.
 *
 * Alternative:
 *   A1) [임시저장] 버튼을 클릭한 경우 → 임시저장 처리
 *   A2) [취소] 버튼을 클릭한 경우 → 메인 화면으로 복귀
 *   A3) 영업 관리자가 반려하는 경우 → 반려 사유 입력 후 반려 알림 발송
 *
 * Exception:
 *   E1) 필수 항목이 누락된 경우 → 오류 메시지 출력
 *   E2) 승인 요청 알림 발송 실패 시 → 재시도 안내
 */
public class EducationPlanRunner {

    public static void run() {
        ConsoleHelper.printDoubleDivider();
        System.out.println("UC: 교육 계획안을 작성한다");
        ConsoleHelper.printDoubleDivider();

        EducationTrainer trainer = Repository.educationTrainers.get(0);
        SalesManager manager = Repository.salesManagers.get(0);

        // 2. 시스템은 교육 계획 작성 화면을 출력한다.
        ConsoleHelper.printStage("시스템", "교육계획안 작성 화면을 출력합니다.");
        ConsoleHelper.printInfo("입력 항목: 교육명 / 채널유형 / 대상자수 / 예산 / 시작일 / 종료일");
        EducationPlan plan = trainer.createEducationPlan();

        // 3. 영업교육 담당자는 교육 기본 정보를 입력한다.
        ConsoleHelper.printStage("영업교육담당자", "교육 기본 정보를 입력합니다.");
        String educationName = ConsoleHelper.readNonEmpty("  교육명: ");
        String channelType = ConsoleHelper.readNonEmpty("  채널유형 (설계사/대리점/TM): ");
        int targetCount = ConsoleHelper.readPositiveInt("  대상자수: ");
        long budget = ConsoleHelper.readLong("  예산 (원): ");
        LocalDate startDate = ConsoleHelper.readDate("  시작일");
        LocalDate endDate = ConsoleHelper.readDate("  종료일");
        plan.enterPlanInfo(educationName, startDate, endDate, channelType, targetCount, budget);

        // 4. 시스템은 필수 항목 누락 여부를 실시간으로 검증한다. (E1)
        ConsoleHelper.printStage("시스템", "필수항목을 검증합니다.");
        if (!plan.validateRequiredFields()) {
            // E1) 필수 항목이 누락된 경우
            ConsoleHelper.printError("[E1] 필수 항목을 입력해주세요.");
            ConsoleHelper.waitEnter();
            return;
        }
        ConsoleHelper.printSuccess("검증 완료.");

        // 5. 영업교육 담당자는 처리 방법을 선택한다. (A1, A2)
        int action = ConsoleHelper.readMenuChoice(
                "[영업교육담당자] 처리를 선택하세요.",
                "승인 요청", "임시저장", "취소");

        if (action == 2) {
            // A1) [임시저장] 버튼을 클릭한 경우
            plan.tempSave();
            Repository.educationPlans.add(plan);
            ConsoleHelper.printSuccess("[A1] 임시저장되었습니다. 계획번호: " + plan.getPlanNumber());
            ConsoleHelper.waitEnter();
            return;
        }

        if (action == 3) {
            // A2) [취소] 버튼을 클릭한 경우
            ConsoleHelper.printInfo("[A2] 작성을 취소하고 메인 화면으로 돌아갑니다.");
            ConsoleHelper.waitEnter();
            return;
        }

        // 6~7. 승인 요청 확인 팝업 출력 및 확인
        ConsoleHelper.printStage("시스템", "영업 관리자에게 승인을 요청하시겠습니까?");
        boolean confirm = ConsoleHelper.readYesNo("  확인");
        if (!confirm) {
            ConsoleHelper.waitEnter();
            return;
        }

        // 8. 시스템은 영업 관리자에게 승인 요청 알림을 자동 발송한다.
        plan.requestApproval();
        Repository.educationPlans.add(plan);
        ConsoleHelper.printStage("시스템", "영업 관리자에게 승인 요청 알림을 발송합니다.");
        ConsoleHelper.printInfo("교육명: " + plan.getEducationName()
                + " | 채널유형: " + plan.getChannelType()
                + " | 예산: " + plan.getBudget() + "원");
        ConsoleHelper.printSuccess("승인 요청 완료. 계획번호: " + plan.getPlanNumber());

        // 9. 영업 관리자는 승인 또는 반려를 선택한다. (A3)
        ConsoleHelper.printDoubleDivider();
        int approveChoice = ConsoleHelper.readMenuChoice(
                "[영업관리자] 교육계획안을 검토합니다.",
                "승인", "반려");

        if (approveChoice == 1) {
            manager.approveEducationPlan(plan);
            // 10. 시스템은 승인 완료 결과를 출력한다.
            ConsoleHelper.printStage("시스템", "승인 완료 결과를 출력합니다.");
            ConsoleHelper.printInfo("계획번호: " + plan.getPlanNumber()
                    + " | 교육명: " + plan.getEducationName()
                    + " | 상태: " + plan.getStatus());
        } else {
            // A3) 영업 관리자가 반려하는 경우
            String reason = ConsoleHelper.readNonEmpty("  [A3] 반려 사유: ");
            manager.rejectEducationPlan(plan, reason);
            ConsoleHelper.printStage("시스템", "반려 알림을 영업교육담당자에게 발송합니다.");
            ConsoleHelper.printInfo("반려 사유: " + reason);
        }

        ConsoleHelper.waitEnter();
    }
}
