package dp.runner.usecase;

import dp.actor.Designer;
import dp.consultation.InsuranceProduct;
import dp.consultation.PolicyApplication;
import dp.runner.ConsoleHelper;
import dp.runner.Repository;
import java.util.List;

/**
 * UC: 청약서를 작성한다 시나리오 진행자
 *
 * Basic Path:
 *   1. 판매채널은 [청약서 작성] 메뉴를 클릭한다.
 *   2. 시스템은 청약서 작성 화면을 출력한다.
 *   3. 판매채널은 고객 기본정보(고객명, 생년월일, 연락처, 주소)를 입력한다.
 *   4. 시스템은 가입 가능한 상품 목록을 출력한다.
 *   5. 판매채널은 상품과 계약 정보(보험기간, 납입방법)를 선택한다.
 *   6. 시스템은 청약서 초안을 출력한다.
 *   7. 판매채널은 서명본을 첨부하고 [제출] 버튼을 클릭한다. (A1, E1)
 *   8. 시스템은 파일 업로드 완료 결과를 출력한다.
 *   9. 판매채널은 [최종 제출] 버튼을 클릭한다.
 *  10. 시스템은 제출 완료 결과(청약번호, 제출일시, 담당 심사자)를 출력한다.
 *
 * Alternative:
 *   A1) 온라인 전자서명인 경우
 *       → 전자서명 요청 링크 발송, 고객 본인인증 후 서명 완료, 보험 심사자에게 자동 전달
 *
 * Exception:
 *   E1) 첨부 파일 업로드 실패 시 → 재시도 안내
 */
public class PolicyApplicationRunner {

    public static void run() {
        ConsoleHelper.printDoubleDivider();
        System.out.println("UC: 청약서를 작성한다");
        ConsoleHelper.printDoubleDivider();

        Designer designer = Repository.designers.get(0);

        // 2. 시스템은 청약서 작성 화면을 출력한다.
        ConsoleHelper.printStage("시스템", "청약서 작성 화면을 출력합니다.");
        ConsoleHelper.printInfo("입력 항목: 고객명 / 생년월일 / 연락처 / 주소");
        PolicyApplication application = designer.createPolicyApplication();

        // 3. 판매채널은 고객 기본정보를 입력한다.
        ConsoleHelper.printStage("판매채널", "고객 기본 정보를 입력합니다.");
        String customerName = ConsoleHelper.readNonEmpty("  고객명: ");
        String birthDate = ConsoleHelper.readNonEmpty("  생년월일 (yyyy-MM-dd): ");
        String contact = ConsoleHelper.readNonEmpty("  연락처: ");
        String address = ConsoleHelper.readNonEmpty("  주소: ");
        application.enterCustomerInfo(customerName, birthDate, contact, address);

        // 4. 시스템은 가입 가능한 상품 목록을 출력한다.
        ConsoleHelper.printStage("시스템", "가입 가능한 보험상품 목록을 출력합니다.");
        List<InsuranceProduct> products = Repository.insuranceProducts;
        for (int i = 0; i < products.size(); i++) {
            ConsoleHelper.printInfo("[" + (i + 1) + "] " + products.get(i).getProductName()
                    + " | 월 " + products.get(i).getMonthlyPremium() + "원");
        }

        // 5. 판매채널은 상품 및 계약 정보를 선택한다.
        int productChoice = ConsoleHelper.readMenuChoice(
                "[판매채널] 상품을 선택하세요.",
                products.stream().map(InsuranceProduct::getProductName).toArray(String[]::new));
        int period = ConsoleHelper.readPositiveInt("  보험기간 (년): ");
        String paymentMethod = ConsoleHelper.readNonEmpty("  납입방법 (월납/연납): ");
        application.selectProduct(products.get(productChoice - 1).getProductName(), period, paymentMethod);

        // 6. 시스템은 청약서 초안을 출력한다.
        ConsoleHelper.printStage("시스템", "청약서 초안을 출력합니다.");
        ConsoleHelper.printInfo("고객명: " + application.getCustomerName()
                + " | 상품명: " + application.getProductName()
                + " | 보험기간: " + application.getPeriod() + "년"
                + " | 납입방법: " + application.getPaymentMethod());

        // 7. 판매채널은 서명 방법을 선택한다. (A1, E1)
        int signChoice = ConsoleHelper.readMenuChoice(
                "[판매채널] 서명 방법을 선택하세요.",
                "서면 서명 파일 첨부", "전자서명 요청 (A1)");

        if (signChoice == 1) {
            application.attachSignature("signature.png");
            // 8. 시스템은 파일 업로드 완료 결과를 출력한다.
            ConsoleHelper.printStage("시스템", "파일 업로드 완료 결과를 출력합니다.");
            ConsoleHelper.printInfo("파일명: signature.png");
        } else {
            // A1) 전자서명 요청
            application.requestElectronicSignature();
            ConsoleHelper.printStage("시스템", "[A1] 전자서명 요청 링크를 고객에게 발송합니다.");
            ConsoleHelper.printInfo("고객이 링크에 접속하여 본인인증 후 전자서명을 완료합니다.");
            ConsoleHelper.printInfo("서명 완료된 청약서를 보험 심사자에게 자동 전달합니다.");
        }

        // 9. 판매채널은 [최종 제출] 버튼을 클릭한다.
        boolean submit = ConsoleHelper.readYesNo("[판매채널] 청약서를 최종 제출하시겠습니까?");
        if (submit) {
            application.submit();
            Repository.policyApplications.add(application);
            // 10. 시스템은 제출 완료 결과를 출력한다.
            ConsoleHelper.printStage("시스템", "제출 완료 결과를 출력합니다.");
            ConsoleHelper.printInfo("청약번호: " + application.getApplicationNumber()
                    + " | 제출일시: " + application.getSubmittedAt()
                    + " | 담당 심사자: " + Repository.insuranceReviewers.get(0).getName());
        }

        ConsoleHelper.waitEnter();
    }
}
