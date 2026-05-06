package dp.sales;

import dp.enums.InsuranceType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CustomerRegistration 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 초기 마스킹 상태, specialClauses 초기화
 * - toggleSsnMask(): 마스킹 토글
 * - getMaskedSsn(): 마스킹 처리된 주민번호 반환
 * - validateRequired(): 필수 항목 검사
 * - validateFormat(): 형식 유효성 검사
 * - assignIds(): 고객번호, 계약번호 자동 부여
 * - addSpecialClause(): 특약 추가
 */
public class CustomerRegistrationTest {

    @Test
    public void 생성_시_주민번호_마스킹이_true이다() {
        CustomerRegistration reg = new CustomerRegistration();
        assertTrue(reg.getIsSsnMasked());
    }

    @Test
    public void 생성_시_특약목록이_초기화된다() {
        CustomerRegistration reg = new CustomerRegistration();
        assertNotNull(reg.getSpecialClauses());
        assertTrue(reg.getSpecialClauses().isEmpty());
    }

    @Test
    public void toggleSsnMask_마스킹이_토글된다() {
        CustomerRegistration reg = new CustomerRegistration();
        assertTrue(reg.getIsSsnMasked());
        reg.toggleSsnMask();
        assertFalse(reg.getIsSsnMasked());
        reg.toggleSsnMask();
        assertTrue(reg.getIsSsnMasked());
    }

    @Test
    public void getMaskedSsn_뒷자리가_마스킹된다() {
        CustomerRegistration reg = new CustomerRegistration();
        reg.setSsn("9001011234567");
        String masked = reg.getMaskedSsn();
        assertEquals("900101-1******", masked);
    }

    @Test
    public void getMaskedSsn_주민번호가_null이면_null반환() {
        CustomerRegistration reg = new CustomerRegistration();
        assertNull(reg.getMaskedSsn());
    }

    @Test
    public void validateRequired_모든_필수값_입력시_true() {
        CustomerRegistration reg = new CustomerRegistration();
        reg.setName("홍길동");
        reg.setSsn("9001011234567");
        reg.setPhone("01012345678");
        reg.setInsuranceType(InsuranceType.LIFE);
        reg.setContractDate(LocalDate.of(2025, 1, 1));
        reg.setExpiryDate(LocalDate.of(2026, 1, 1));
        reg.setMonthlyPremium(100000L);
        assertTrue(reg.validateRequired());
    }

    @Test
    public void validateRequired_이름_누락시_false() {
        CustomerRegistration reg = new CustomerRegistration();
        reg.setSsn("9001011234567");
        reg.setPhone("01012345678");
        reg.setInsuranceType(InsuranceType.LIFE);
        reg.setContractDate(LocalDate.now());
        reg.setExpiryDate(LocalDate.now().plusYears(1));
        reg.setMonthlyPremium(100000L);
        assertFalse(reg.validateRequired());
    }

    @Test
    public void validateRequired_월보험료_0이하면_false() {
        CustomerRegistration reg = new CustomerRegistration();
        reg.setName("홍길동");
        reg.setSsn("9001011234567");
        reg.setPhone("01012345678");
        reg.setInsuranceType(InsuranceType.HEALTH);
        reg.setContractDate(LocalDate.now());
        reg.setExpiryDate(LocalDate.now().plusYears(1));
        reg.setMonthlyPremium(0L);
        assertFalse(reg.validateRequired());
    }

    @Test
    public void validateFormat_올바른_주민번호와_전화번호_true() {
        CustomerRegistration reg = new CustomerRegistration();
        reg.setSsn("9001011234567");
        reg.setPhone("01012345678");
        assertTrue(reg.validateFormat());
    }

    @Test
    public void validateFormat_하이픈_포함_주민번호_true() {
        CustomerRegistration reg = new CustomerRegistration();
        reg.setSsn("900101-1234567");
        reg.setPhone("010-1234-5678");
        assertTrue(reg.validateFormat());
    }

    @Test
    public void validateFormat_주민번호_자리수_부족시_false() {
        CustomerRegistration reg = new CustomerRegistration();
        reg.setSsn("12345");
        reg.setPhone("01012345678");
        assertFalse(reg.validateFormat());
    }

    @Test
    public void validateFormat_전화번호_자리수_부족시_false() {
        CustomerRegistration reg = new CustomerRegistration();
        reg.setSsn("9001011234567");
        reg.setPhone("0101234");
        assertFalse(reg.validateFormat());
    }

    @Test
    public void assignIds_고객번호와_계약번호가_부여된다() {
        CustomerRegistration reg = new CustomerRegistration();
        reg.assignIds();
        assertNotNull(reg.getCustomerId());
        assertNotNull(reg.getContractNo());
        assertTrue(reg.getCustomerId().startsWith("CU-"));
        assertTrue(reg.getContractNo().startsWith("CN-"));
    }

    @Test
    public void addSpecialClause_특약이_추가된다() {
        CustomerRegistration reg = new CustomerRegistration();
        reg.addSpecialClause("암진단특약");
        reg.addSpecialClause("입원특약");
        assertEquals(2, reg.getSpecialClauses().size());
        assertTrue(reg.getSpecialClauses().contains("암진단특약"));
    }
}