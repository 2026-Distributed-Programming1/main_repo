package dp.consultation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Revival 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 부활번호 자동 부여
 * - checkEligibility(): 부활 가능 여부 확인
 * - calculateUnpaidAmount(): 미납보험료 산출
 * - pay(): 납입 처리
 * - authenticate(): 본인인증
 * - submit(): 부활 신청 및 신청일시 설정
 */
public class RevivalTest {

    @Test
    public void 생성_시_부활번호가_부여된다() {
        Revival revival = new Revival();
        assertTrue(revival.getRevivalNumber() > 0);
    }

    @Test
    public void 부활가능여부_확인이_정상_동작한다() {
        Revival revival = new Revival();
        assertTrue(revival.checkEligibility());
    }

    @Test
    public void 미납보험료_설정이_정상_반영된다() {
        Revival revival = new Revival();
        revival.setUnpaidAmount(150000L);
        assertEquals(150000L, revival.getUnpaidAmount());
    }

    @Test
    public void 미납보험료_산출이_정상_동작한다() {
        Revival revival = new Revival();
        revival.setUnpaidAmount(150000L);
        long amount = revival.calculateUnpaidAmount();
        assertEquals(150000L, amount);
    }

    @Test
    public void 납입처리가_정상_동작한다() {
        Revival revival = new Revival();
        revival.setUnpaidAmount(150000L);
        assertDoesNotThrow(() -> revival.pay("카드"));
        assertEquals("카드", revival.getPaymentMethod());
    }

    @Test
    public void 본인인증이_정상_동작한다() {
        Revival revival = new Revival();
        assertTrue(revival.authenticate());
    }

    @Test
    public void submit_후_신청일시가_설정된다() {
        Revival revival = new Revival();
        revival.setUnpaidAmount(150000L);
        revival.pay("카드");
        revival.authenticate();
        revival.submit();

        assertNotNull(revival.getAppliedAt());
    }
}