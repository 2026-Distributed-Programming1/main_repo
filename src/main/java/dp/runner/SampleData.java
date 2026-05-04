package dp.runner;

import dp.actor.*;
import dp.common.BankAccount;
import dp.consultation.InsuranceProduct;
import dp.contract.InsuranceContract;
import dp.payment.OverdueNoticeSetting;

import java.time.LocalDate;

/**
 * 샘플 데이터 생성 (유스케이스 외부의 구동 코드)
 * <p>
 * 프로그램 시작 시 시연을 위해 기본 객체들을 메모리에 등록한다.
 * 각 유스케이스가 시작될 때 새로 입력받지 않아도 되도록 도와준다.
 */
public class SampleData {

    private SampleData() {
    }

    public static void initialize() {
        createCustomers();
        createEmployees();
        createContracts();
        createSettings();
        System.out.println("[시스템] 샘플 데이터 초기화 완료");
        // ===== 교육/상담 도메인 샘플 데이터 =====

        // 액터
        Repository.educationTrainers.add(new EducationTrainer("김영교", "010-1111-2222", "trainer@ins.com"));
        Repository.salesManagers.add(new SalesManager("이영관", "010-3333-4444", "manager@ins.com"));
        Repository.insuranceReviewers.add(new InsuranceReviewer("박심사", "010-5555-6666", "reviewer@ins.com"));
        Repository.designers.add(new Designer(1, "최설계", "서울", "L-2024-001"));
        Repository.agencies.add(new Agency(2, "한국대리점", "부산", "A-2024-001"));

        // 보험상품 샘플
        Repository.insuranceProducts.add(new InsuranceProduct(
                "실손의료보험", "건강", 50000, "의료비 전액 보장", "치과 제외"));
        Repository.insuranceProducts.add(new InsuranceProduct(
                "종신보험", "생명", 150000, "사망 시 1억 지급", "없음"));
        Repository.insuranceProducts.add(new InsuranceProduct(
                "자동차보험", "손해", 80000, "대인/대물 무제한", "음주운전 제외"));

    }

    private static void createCustomers() {
        Customer c1 = new Customer("김고객", "900101-1234567", "010-1111-2222", "kim@test.com");
        c1.enterAddress("서울시 강남구 테헤란로 123");
        c1.enterBirthDate(LocalDate.of(1990, 1, 1));
        BankAccount a1 = new BankAccount();
        a1.enter("국민은행", "123-456-789012", "김고객");
        a1.verify();
        c1.registerAccount(a1);

        Customer c2 = new Customer("이고객", "850515-2345678", "010-3333-4444", "lee@test.com");
        c2.enterAddress("서울시 서초구 반포대로 45");
        c2.enterBirthDate(LocalDate.of(1985, 5, 15));
        BankAccount a2 = new BankAccount();
        a2.enter("신한은행", "987-654-321098", "이고객");
        a2.verify();
        c2.registerAccount(a2);

        Customer c3 = new Customer("최고객", "950820-1456789", "010-5555-6666", "choi@test.com");
        c3.enterAddress("경기도 성남시 분당구");
        c3.enterBirthDate(LocalDate.of(1995, 8, 20));
        BankAccount a3 = new BankAccount();
        a3.enter("우리은행", "111-222-333444", "최고객");
        a3.verify();
        c3.registerAccount(a3);

        Repository.customers.add(c1);
        Repository.customers.add(c2);
        Repository.customers.add(c3);
    }

    private static void createEmployees() {
        // 보상담당자
        ClaimsHandler h1 = new ClaimsHandler("박보상", "보상팀", "대리", 5_000_000L);
        ClaimsHandler h2 = new ClaimsHandler("정보상", "보상팀", "과장", 20_000_000L);
        Repository.claimsHandlers.add(h1);
        Repository.claimsHandlers.add(h2);

        // 현장출동 직원
        DispatchAgent d1 = new DispatchAgent("이출동", "현장출동팀", "사원", "강남", "12가3456");
        DispatchAgent d2 = new DispatchAgent("강출동", "현장출동팀", "사원", "분당", "34나5678");
        Repository.dispatchAgents.add(d1);
        Repository.dispatchAgents.add(d2);

        // 재무회계 담당자
        FinanceManager f1 = new FinanceManager("정재무", "재무팀", "과장");
        Repository.financeManagers.add(f1);
    }

    private static void createContracts() {
        // 김고객의 자동차 보험 계약
        InsuranceContract con1 = new InsuranceContract(
                Repository.customers.get(0),
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2033, 1, 1),
                500_000L);

        // 김고객의 건강 보험 계약
        InsuranceContract con2 = new InsuranceContract(
                Repository.customers.get(0),
                LocalDate.of(2022, 6, 1),
                LocalDate.of(2032, 6, 1),
                100_000L);

        // 이고객의 종신 보험 계약
        InsuranceContract con3 = new InsuranceContract(
                Repository.customers.get(1),
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2054, 3, 1),
                300_000L);

        // 최고객의 자동차 보험 계약
        InsuranceContract con4 = new InsuranceContract(
                Repository.customers.get(2),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2034, 1, 1),
                200_000L);

        Repository.contracts.add(con1);
        Repository.contracts.add(con2);
        Repository.contracts.add(con3);
        Repository.contracts.add(con4);
    }

    private static void createSettings() {
        Repository.overdueNoticeSetting = new OverdueNoticeSetting();
    }
}
