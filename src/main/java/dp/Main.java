package dp;

import dp.runner.ConsoleHelper;
import dp.runner.SampleData;
import dp.runner.usecase.*;

/**
 * 인터랙티브 메인 진입점
 * <p>
 * 사용자가 메뉴에서 유스케이스를 선택하면 해당 시나리오 진행자(Runner)를 실행한다. 도메인 클래스는 일절 변경하지 않으며, 본 파일과 runner 패키지는 모두 클래스 다이어그램 외부의 "구동 코드"이다.
 *
 * Runner는 Usecase Scenario, Usecase Diagram을 참고
 * Main이 Usecase Diagram 참고
 * 다른 클래스들은 Class Diagram 참고
 */
public class Main {

    public static void main(String[] args) {
        ConsoleHelper.printDoubleDivider();
        System.out.println("  보험 시스템 시나리오 시뮬레이터");
        System.out.println("  (사고/보험금 + 납입/환급 도메인)");
        ConsoleHelper.printDoubleDivider();

        // 샘플 데이터 초기화
        SampleData.initialize();

        // 메인 메뉴 루프
        while (true) {
            int choice = ConsoleHelper.readMenuChoice(
                    "\n=== 메인 메뉴: 실행할 유스케이스를 선택하세요 ===",
                    "[7-UC1] 사고를 접수한다",
                    "[7-UC2] 현장 출동 정보를 기록한다",
                    "[7-UC3] 보험금을 요청한다",
                    "[7-UC4] 손해 조사를 한다  (→ UC5 산출 → UC6 지급 자동 연결)",
                    "[8] 가입 보험을 조회한다  (→ A2 납입 · A3 해지 연결)",
                    "[8-UC8] 납부 내역을 관리한다",
                    "[8] 해약 환급 내역을 조회한다  (UC9 산출 · UC10 지급 포함)",
                    "[4-UC1] 교육 계획안을 작성한다",
                    "[4-UC2] 교육 제반을 등록한다",
                    "[4-UC4] 상담을 요청한다",
                    "[4-UC5] 면담일정을 관리한다",
                    "[4-UC6] 면담기록을 관리한다",
                    "[4-UC8] 청약서를 작성한다",
                    "[4-UC11] 인수 심사를 한다",
                    "[4] 보험상품을 조회한다  (→ A3 보험 신청 · A4 부활 신청 연결)",
                    "영업 활동을 관리한다",
                    "영업조직을 평가한다",
                    "판매채널 채용을 심사한다",
                    "고객 정보를 등록한다",
                    "활동 계획을 작성한다",
                    "계약 정보를 조회한다",
                    "문의한다",
                    "종료");

            switch (choice) {
                case 1:
                    AccidentReportRunner.run();
                    break;
                case 2:
                    DispatchRecordRunner.run();
                    break;
                case 3:
                    ClaimRequestRunner.run();
                    break;
                case 4:
                    DamageInvestigationRunner.run();
                    break;
                case 5:
                    MyInsuranceViewRunner.run();
                    break;
                case 6:
                    PaymentRecordRunner.run();
                    break;
                case 7:
                    RefundListRunner.run();
                    break;
                case 8:
                    EducationPlanRunner.run();
                    break;
                case 9:
                    EducationPreparationRunner.run();
                    break;
                // [4-UC3] 교육을 진행한다 → EducationPreparationRunner 내 extend로 연결
                case 10:
                    ConsultationRequestRunner.run();
                    break;
                case 11:
                    InterviewScheduleRunner.run();
                    break;
                case 12:
                    InterviewRecordRunner.run();
                    break;
                // [4-UC7] 보험상품을 제안한다 → InterviewRecordRunner 내 include로 연결
                case 13:
                    PolicyApplicationRunner.run();
                    break;
                case 14:
                    UnderwritingRunner.run();
                    break;
                // [4-UC9] 보험을 신청한다 → InsuranceProductInquiryRunner 내 extend로 연결
                // [4-UC10] 부활을 요청한다 → InsuranceProductInquiryRunner 내 extend로 연결
                case 15:
                    InsuranceProductInquiryRunner.run();
                    break;
                // 판매채널을 모집한다 → SalesActivityRunner 내 extend로 연결
                // 성과급 지급을 요청한다 → SalesOrgEvaluationRunner 내 extend로 연결
                case 16:
                    SalesActivityRunner.run();
                    break;
                case 17:
                    SalesOrgEvaluationRunner.run();
                    break;
                case 18:
                    ChannelScreeningRunner.run();
                    break;
                case 19:
                    CustomerRegistrationRunner.run();
                    break;
                case 20:
                    ActivityPlanRunner.run();
                    break;
                case 21:
                    ContractInfoRunner.run();
                    break;
                // 계약 통계 정보를 관리한다 → ContractInfoRunner 내 extend로 연결
                case 22:
                    InquiryRunner.run();
                    break;
                case 23:
                    System.out.println("\n프로그램을 종료합니다.");
                    return;
            }
        }
    }
}