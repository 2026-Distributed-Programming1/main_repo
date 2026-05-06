package dp.contract;

import dp.actor.Customer;
import dp.enums.ContractStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contract 단위 테스트
 *
 * 검증 대상:
 * - 파라미터 생성자: 계약번호·증권번호 자동 부여, 초기 상태(NORMAL)
 * - 기본 생성자: 기본 상태(NORMAL) 초기화
 * - isMaturityNear(): 만기 30일 이내 여부 판별 (A3 흐름)
 * - updateStatus(): 계약 상태 갱신
 */
public class ContractTest {

    private Customer customer;

    @BeforeEach
    public void setUp() {
        customer = new Customer("홍길동", "9001011234567", "01012345678", "test@test.com");
    }

    // ── 파라미터 생성자 ───────────────────────────────────────────

    @Test
    public void 파라미터_생성자_계약번호가_자동_부여된다() {
        Contract contract = new Contract(customer, LocalDate.now(), LocalDate.now().plusYears(1), 100000L);
        assertNotNull(contract.getContractNo());
        assertTrue(contract.getContractNo().startsWith("CON"));
    }

    @Test
    public void 파라미터_생성자_증권번호가_자동_부여된다() {
        Contract contract = new Contract(customer, LocalDate.now(), LocalDate.now().plusYears(1), 100000L);
        assertNotNull(contract.getPolicyNo());
        assertTrue(contract.getPolicyNo().startsWith("POL"));
    }

    @Test
    public void 파라미터_생성자_초기_상태는_NORMAL이다() {
        Contract contract = new Contract(customer, LocalDate.now(), LocalDate.now().plusYears(1), 100000L);
        assertEquals(ContractStatus.NORMAL, contract.getStatus());
    }

    @Test
    public void 파라미터_생성자_고객정보가_설정된다() {
        Contract contract = new Contract(customer, LocalDate.now(), LocalDate.now().plusYears(1), 100000L);
        assertEquals(customer, contract.getCustomer());
    }

    @Test
    public void 파라미터_생성자_월_보험료가_설정된다() {
        Contract contract = new Contract(customer, LocalDate.now(), LocalDate.now().plusYears(1), 200000L);
        assertEquals(200000L, contract.getMonthlyPremium());
    }

    // ── 기본 생성자 ──────────────────────────────────────────────

    @Test
    public void 기본_생성자_초기_상태는_NORMAL이다() {
        Contract contract = new Contract();
        assertEquals(ContractStatus.NORMAL, contract.getStatus());
    }

    @Test
    public void 기본_생성자_특약목록이_초기화된다() {
        Contract contract = new Contract();
        assertNotNull(contract.getSpecialClauses());
        assertTrue(contract.getSpecialClauses().isEmpty());
    }

    // ── isMaturityNear() — 시나리오 A3: 만기 30일 이내 ──────────

    @Test
    public void isMaturityNear_만기_30일_이내이면_true() {
        Contract contract = new Contract();
        contract.setEndDate(LocalDate.now().plusDays(15));
        assertTrue(contract.isMaturityNear());
    }

    @Test
    public void isMaturityNear_만기_정확히_30일_남으면_true() {
        Contract contract = new Contract();
        contract.setEndDate(LocalDate.now().plusDays(30));
        assertTrue(contract.isMaturityNear());
    }

    @Test
    public void isMaturityNear_만기_당일이면_true() {
        Contract contract = new Contract();
        contract.setEndDate(LocalDate.now());
        assertTrue(contract.isMaturityNear());
    }

    @Test
    public void isMaturityNear_만기_31일_이상_남으면_false() {
        Contract contract = new Contract();
        contract.setEndDate(LocalDate.now().plusDays(31));
        assertFalse(contract.isMaturityNear());
    }

    @Test
    public void isMaturityNear_만기일이_이미_지났으면_false() {
        Contract contract = new Contract();
        contract.setEndDate(LocalDate.now().minusDays(1));
        assertFalse(contract.isMaturityNear());
    }

    @Test
    public void isMaturityNear_만기일이_null이면_false() {
        Contract contract = new Contract();
        assertFalse(contract.isMaturityNear());
    }

    // ── updateStatus() ───────────────────────────────────────────

    @Test
    public void updateStatus_정상으로_변경된다() {
        Contract contract = new Contract();
        contract.setStatus(ContractStatus.EXPIRED);
        contract.updateStatus("정상");
        assertEquals(ContractStatus.NORMAL, contract.getStatus());
    }

    @Test
    public void updateStatus_정상유지로_변경된다() {
        Contract contract = new Contract();
        contract.updateStatus("정상유지");
        assertEquals(ContractStatus.NORMAL, contract.getStatus());
    }

    @Test
    public void updateStatus_만기로_변경된다() {
        Contract contract = new Contract();
        contract.updateStatus("만기");
        assertEquals(ContractStatus.EXPIRED, contract.getStatus());
    }

    @Test
    public void updateStatus_알_수_없는_값은_상태를_변경하지_않는다() {
        Contract contract = new Contract();
        contract.updateStatus("알수없음");
        assertEquals(ContractStatus.NORMAL, contract.getStatus());
    }

    // ── startDate / endDate alias ────────────────────────────────

    @Test
    public void startDate와_contractDate는_같은_값이다() {
        Contract contract = new Contract();
        LocalDate date = LocalDate.of(2025, 1, 1);
        contract.setStartDate(date);
        assertEquals(date, contract.getContractDate());
        assertEquals(date, contract.getStartDate());
    }

    @Test
    public void endDate와_expiryDate는_같은_값이다() {
        Contract contract = new Contract();
        LocalDate date = LocalDate.of(2026, 1, 1);
        contract.setEndDate(date);
        assertEquals(date, contract.getExpiryDate());
        assertEquals(date, contract.getEndDate());
    }
}