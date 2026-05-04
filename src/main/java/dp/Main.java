package com.insurance;

import com.insurance.runner.ConsoleHelper;
import com.insurance.runner.SampleData;
import com.insurance.runner.usecase.AccidentReportRunner;
import com.insurance.runner.usecase.ClaimCalculationRunner;
import com.insurance.runner.usecase.ClaimPaymentRunner;
import com.insurance.runner.usecase.ClaimRequestRunner;
import com.insurance.runner.usecase.DamageInvestigationRunner;
import com.insurance.runner.usecase.DispatchRecordRunner;
import com.insurance.runner.usecase.PaymentRecordRunner;
import com.insurance.runner.usecase.PaymentRunner;
import com.insurance.runner.usecase.RefundCalculationRunner;
import com.insurance.runner.usecase.RefundPaymentRunner;

/**
 * 인터랙티브 메인 진입점
 *
 * 사용자가 메뉴에서 유스케이스를 선택하면 해당 시나리오 진행자(Runner)를 실행한다.
 * 도메인 클래스는 일절 변경하지 않으며, 본 파일과 runner 패키지는 모두
 * 클래스 다이어그램 외부의 "구동 코드"이다.
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
                    "[7-UC4] 손해 조사를 한다",
                    "[7-UC5] 보험금을 산출한다",
                    "[7-UC6] 보험금을 지급한다",
                    "[8-UC7] 보험료를 납입한다",
                    "[8-UC8] 납부 내역을 관리한다",
                    "[8-UC9] 해약 환급금을 산출한다",
                    "[8-UC10] 해약 환급금을 지급한다",
                    "종료");

            switch (choice) {
                case 1: AccidentReportRunner.run(); break;
                case 2: DispatchRecordRunner.run(); break;
                case 3: ClaimRequestRunner.run(); break;
                case 4: DamageInvestigationRunner.run(); break;
                case 5: ClaimCalculationRunner.run(); break;
                case 6: ClaimPaymentRunner.run(); break;
                case 7: PaymentRunner.run(); break;
                case 8: PaymentRecordRunner.run(); break;
                case 9: RefundCalculationRunner.run(); break;
                case 10: RefundPaymentRunner.run(); break;
                case 11:
                    System.out.println("\n프로그램을 종료합니다.");
                    return;
            }
        }
    }
}
