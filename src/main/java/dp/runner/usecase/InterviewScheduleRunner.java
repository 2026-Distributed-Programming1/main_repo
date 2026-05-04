package dp.runner.usecase;

import dp.consultation.InterviewSchedule;
import dp.runner.ConsoleHelper;
import dp.runner.Repository;
import java.time.LocalDateTime;

/**
 * UC: 면담일정을 관리한다 시나리오 진행자
 *
 * Basic Path:
 *   1. 판매채널은 [면담일정 관리] 메뉴를 선택한다.
 *   2. 시스템은 면담일정 관리 화면을 출력한다. (오늘 날짜 기준 예정 목록)
 *   3. 판매채널은 조회 조건을 입력하고 [조회] 버튼을 클릭한다. (A1)
 *   4. 시스템은 조회 조건에 맞는 면담일정 목록을 출력한다. (A6)
 *   5. 판매채널은 면담 항목을 클릭한다.
 *   6. 시스템은 선택한 면담의 상세 정보 화면을 출력한다.
 *   7. 판매채널은 [닫기] 버튼을 클릭한다. (A4, A5)
 *   8. 시스템은 면담일정 목록 화면으로 돌아간다.
 *
 * Alternative:
 *   A1) [새 면담 등록] 버튼을 클릭한 경우 → 면담 등록 화면 출력 (A2, A3)
 *   A2) 전화 면담을 선택한 경우 → 연락 가능 시간대, 준비사항 입력
 *   A3) 온라인 면담을 선택한 경우 → 희망 채널, 면담일시, 준비사항 입력
 *   A4) [수정] 버튼을 클릭한 경우 → 면담 정보 수정 (E2)
 *   A5) [취소] 버튼을 클릭한 경우 → 면담 상태를 취소로 변경
 *   A6) 조회 조건에 맞는 면담 일정이 없는 경우 → 안내 메시지 출력
 *
 * Exception:
 *   E1) 등록 시 필수 항목(고객명, 면담유형, 면담일시)이 입력되지 않은 경우
 *   E2) 수정 시 필수 항목(면담유형, 면담일시)이 입력되지 않은 경우
 */
public class InterviewScheduleRunner {

    public static void run() {
        ConsoleHelper.printDoubleDivider();
        System.out.println("UC: 면담일정을 관리한다");
        ConsoleHelper.printDoubleDivider();

        // 2. 시스템은 면담일정 관리 화면을 출력한다.
        ConsoleHelper.printStage("시스템", "면담일정 관리 화면을 출력합니다.");
        if (!Repository.interviewSchedules.isEmpty()) {
            ConsoleHelper.printInfo("면담 목록 (고객명 / 유형 / 상태):");
            for (InterviewSchedule s : Repository.interviewSchedules) {
                ConsoleHelper.printInfo("[" + s.getInterviewNumber() + "] "
                        + s.getCustomerName()
                        + " | " + s.getType()
                        + " | " + s.getStatus());
            }
        } else {
            ConsoleHelper.printInfo("등록된 면담 일정이 없습니다.");
        }

        // 3. 판매채널은 작업을 선택한다. (A1)
        int action = ConsoleHelper.readMenuChoice(
                "[판매채널] 작업을 선택하세요.",
                "새 면담 등록 (A1)", "면담 수정 (A4)", "면담 취소 (A5)", "닫기");

        if (action == 4) {
            // 7~8. [닫기] → 목록 화면으로 복귀
            ConsoleHelper.printStage("시스템", "면담일정 목록 화면으로 돌아갑니다.");
            ConsoleHelper.waitEnter();
            return;
        }

        if (action == 1) {
            // A1) [새 면담 등록] 버튼을 클릭한 경우
            ConsoleHelper.printStage("시스템", "면담 등록 화면을 출력합니다.");
            InterviewSchedule schedule = new InterviewSchedule();

            String customerName = ConsoleHelper.readNonEmpty("  고객명: ");

            // A2, A3 분기
            int typeChoice = ConsoleHelper.readMenuChoice(
                    "[판매채널] 면담 유형을 선택하세요.",
                    "방문 면담", "전화 면담 (A2)", "온라인 면담 (A3)");
            String type = switch (typeChoice) {
                case 1 -> "방문";
                case 2 -> "전화";
                case 3 -> "온라인";
                default -> "방문";
            };
            schedule.setType(type);

            LocalDateTime scheduledAt = ConsoleHelper.readDateTime("  희망 면담일시");
            String location = type.equals("방문")
                    ? ConsoleHelper.readNonEmpty("  면담 장소: ")
                    : "";
            String preparation = ConsoleHelper.readLine("  면담 준비사항 (없으면 엔터): ");

            // E1) 필수 항목 검증
            if (customerName.isEmpty() || scheduledAt == null) {
                ConsoleHelper.printError("[E1] 필수 항목을 입력해 주세요.");
                ConsoleHelper.waitEnter();
                return;
            }

            schedule.register(customerName, scheduledAt, location, preparation);
            Repository.interviewSchedules.add(schedule);

            // 5. 시스템은 등록 완료 결과를 출력한다.
            ConsoleHelper.printStage("시스템", "면담 등록 완료 결과를 출력합니다.");
            ConsoleHelper.printInfo("면담번호: " + schedule.getInterviewNumber()
                    + " | 고객명: " + schedule.getCustomerName()
                    + " | 면담일시: " + schedule.getScheduledAt());

            // 6. 판매채널은 [고객 알림 발송] 버튼을 클릭한다.
            boolean sendNotice = ConsoleHelper.readYesNo("[판매채널] 고객에게 면담 일정 알림을 발송하시겠습니까?");
            if (sendNotice) {
                schedule.sendNotice();
            }

        } else if (action == 2) {
            // A4) [수정] 버튼을 클릭한 경우
            if (Repository.interviewSchedules.isEmpty()) {
                ConsoleHelper.printError("수정할 면담 일정이 없습니다.");
                ConsoleHelper.waitEnter();
                return;
            }
            ConsoleHelper.printStage("시스템", "면담 정보를 편집 가능한 상태로 출력합니다.");
            InterviewSchedule schedule = Repository.interviewSchedules.get(
                    Repository.interviewSchedules.size() - 1);

            LocalDateTime scheduledAt = ConsoleHelper.readDateTime("  변경할 면담일시");
            String location = ConsoleHelper.readNonEmpty("  변경할 장소: ");
            String preparation = ConsoleHelper.readLine("  준비사항 (없으면 엔터): ");

            // E2) 수정 시 필수 항목 검증
            if (scheduledAt == null) {
                ConsoleHelper.printError("[E2] 필수 항목을 입력해 주세요.");
                ConsoleHelper.waitEnter();
                return;
            }
            schedule.modify(scheduledAt, location, preparation);

            // 5. 시스템은 수정 완료 결과를 출력한다.
            ConsoleHelper.printStage("시스템", "수정 완료 결과를 출력합니다.");
            ConsoleHelper.printInfo("면담번호: " + schedule.getInterviewNumber());

        } else if (action == 3) {
            // A5) [취소] 버튼을 클릭한 경우
            if (Repository.interviewSchedules.isEmpty()) {
                ConsoleHelper.printError("취소할 면담 일정이 없습니다.");
                ConsoleHelper.waitEnter();
                return;
            }
            ConsoleHelper.printStage("시스템", "해당 면담을 취소하시겠습니까?");
            boolean confirm = ConsoleHelper.readYesNo("  확인");
            if (confirm) {
                InterviewSchedule schedule = Repository.interviewSchedules.get(
                        Repository.interviewSchedules.size() - 1);
                schedule.cancel();
                ConsoleHelper.printStage("시스템", "취소 완료 결과를 출력합니다.");
                ConsoleHelper.printInfo("면담번호: " + schedule.getInterviewNumber()
                        + " | 상태: " + schedule.getStatus());
            }
        }

        ConsoleHelper.waitEnter();
    }
}
