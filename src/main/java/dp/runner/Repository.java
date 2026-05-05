package dp.runner;

import dp.actor.ClaimsHandler;
import dp.actor.Customer;
import dp.actor.DispatchAgent;
import dp.actor.FinanceManager;
import dp.claim.AccidentReport;
import dp.claim.ClaimCalculation;
import dp.claim.ClaimPayment;
import dp.claim.ClaimRequest;
import dp.claim.DamageInvestigation;
import dp.claim.Dispatch;
import dp.claim.DispatchRecord;
import dp.contract.Cancellation;
import dp.contract.Contract;
import dp.contract.ContractStatistics;
import dp.contract.InsuranceContract;
import dp.inquiry.Inquiry;
import dp.payment.OverdueNoticeSetting;
import dp.payment.Payment;
import dp.payment.PaymentRecord;
import dp.payment.RefundCalculation;
import dp.payment.RefundPayment;
import java.util.ArrayList;
import java.util.List;
import dp.actor.Agency;
import dp.actor.Designer;
import dp.actor.EducationTrainer;
import dp.actor.InsuranceReviewer;
import dp.actor.SalesManager;
import dp.consultation.ConsultationRequest;
import dp.consultation.InsuranceApplication;
import dp.consultation.InsuranceProduct;
import dp.consultation.InterviewRecord;
import dp.consultation.InterviewSchedule;
import dp.consultation.PolicyApplication;
import dp.consultation.Proposal;
import dp.consultation.Revival;
import dp.consultation.Underwriting;
import dp.education.EducationExecution;
import dp.education.EducationPlan;
import dp.education.EducationPreparation;
import dp.sales.*;

/**
 * 메모리 객체 보관소 (유스케이스 외부의 구동 코드)
 *
 * 시나리오 진행 중 생성되는 도메인 객체들을 메모리에 보관한다.
 * 프로그램이 종료되면 모든 데이터가 사라진다.
 * 본래 ORM/DB가 담당할 영역이지만 학부 시연 목적이므로 단순화한다.
 */
public class Repository {

    // ===== 행위자 =====
    public static final List<Customer> customers = new ArrayList<>();
    public static final List<ClaimsHandler> claimsHandlers = new ArrayList<>();
    public static final List<DispatchAgent> dispatchAgents = new ArrayList<>();
    public static final List<FinanceManager> financeManagers = new ArrayList<>();

    // ===== 6️⃣ 도메인 (참조용) =====
    public static final List<InsuranceContract> contracts = new ArrayList<>();
    public static final List<Cancellation> cancellations = new ArrayList<>();

    public static final List<Contract> contractInfos = new ArrayList<>();
    public static final List<ContractStatistics> contractStatisticsList = new ArrayList<>();
    // ===== 7️⃣ 도메인 =====
    public static final List<AccidentReport> accidentReports = new ArrayList<>();
    public static final List<Dispatch> dispatches = new ArrayList<>();
    public static final List<DispatchRecord> dispatchRecords = new ArrayList<>();
    public static final List<ClaimRequest> claimRequests = new ArrayList<>();
    public static final List<DamageInvestigation> investigations = new ArrayList<>();
    public static final List<ClaimCalculation> calculations = new ArrayList<>();
    public static final List<ClaimPayment> claimPayments = new ArrayList<>();

    // ===== 8️⃣ 도메인 =====
    public static final List<Payment> payments = new ArrayList<>();
    public static final List<PaymentRecord> paymentRecords = new ArrayList<>();
    public static OverdueNoticeSetting overdueNoticeSetting; // 시스템 단위 설정 (1개)
    public static final List<RefundCalculation> refundCalculations = new ArrayList<>();
    public static final List<RefundPayment> refundPayments = new ArrayList<>();

    private Repository() {}

    // ===== 교육/상담/가입 도메인 액터 =====
    public static final List<EducationTrainer> educationTrainers = new ArrayList<>();
    public static final List<SalesManager> salesManagers = new ArrayList<>();
    public static final List<InsuranceReviewer> insuranceReviewers = new ArrayList<>();
    public static final List<Designer> designers = new ArrayList<>();
    public static final List<Agency> agencies = new ArrayList<>();

    // ===== 교육 도메인 =====
    public static final List<EducationPlan> educationPlans = new ArrayList<>();
    public static final List<EducationPreparation> educationPreparations = new ArrayList<>();
    public static final List<EducationExecution> educationExecutions = new ArrayList<>();

    // ===== 상담 및 가입 도메인 =====
    public static final List<ConsultationRequest> consultationRequests = new ArrayList<>();
    public static final List<InterviewSchedule> interviewSchedules = new ArrayList<>();
    public static final List<InterviewRecord> interviewRecords = new ArrayList<>();
    public static final List<InsuranceProduct> insuranceProducts = new ArrayList<>();
    public static final List<Proposal> proposals = new ArrayList<>();
    public static final List<PolicyApplication> policyApplications = new ArrayList<>();
    public static final List<InsuranceApplication> insuranceApplications = new ArrayList<>();
    public static final List<Revival> revivals = new ArrayList<>();
    public static final List<Underwriting> underwritings = new ArrayList<>();

    // ===== 영업 활동 및 관리 도메인 =====
    public static final List<ChannelRecruitment> channelRecruitments = new ArrayList<>();
    public static final List<BonusRequest> bonusRequests = new ArrayList<>();
    public static final List<SalesActivityManagement> salesActivityManagements = new ArrayList<>();
    public static final List<SalesOrgEvaluation> salesOrgEvaluations = new ArrayList<>();
    public static final List<ChannelScreening> channelScreenings = new ArrayList<>();
    public static final List<CustomerRegistration> customerRegistrations = new ArrayList<>();
    public static final List<ActivityPlan> activityPlans = new ArrayList<>();

    // ===== 고객 문의 도메인 =====
    public static final List<Inquiry> inquiries = new ArrayList<>();
}
