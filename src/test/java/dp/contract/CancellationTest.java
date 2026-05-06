package dp.contract;

import dp.actor.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cancellation 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 해지번호 자동 부여, 초기 상태("작성중")
 * - selectReason() / validateReasonInput(): 해지 사유 및 기타 시 상세사유 검증 (A1 흐름)
 * - agreeToNotice(): 유의사항 동의
 * - authenticate(): 본인인증
 * - calculateExpectedRefund(): 예상 환급금 산출
 * - submit(): 해지 완료 처리 (status="완료", canceledAt 설정)
 * - handleSubmitError(): 오류 처리 (E2 흐름)
 * - cancel(): 중간 취소 (A2 흐름)
 */
public class CancellationTest {

    private Contract contract;

    @BeforeEach
    public void setUp() {
        Customer customer = new Customer("홍길동", "9001011234567", "01012345678", "test@test.com");
        contract = new Contract(customer, LocalDate.now().minusYears(1), LocalDate.now().plusYears(1), 100000L);
    }

    // ── 생성자 ────────────────────────────────────────────────────

    @Test
    public void 생성_시_해지번호가_자동_부여된다() {
        Cancellation cancellation = new Cancellation(contract);
        assertNotNull(cancellation.getCancellationNo());
        assertTrue(cancellation.getCancellationNo().startsWith("CAN"));
    }

    @Test
    public void 생성_시_초기_상태는_작성중이다() {
        Cancellation cancellation = new Cancellation(contract);
        assertEquals("작성중", cancellation.getStatus());
    }

    @Test
    public void 생성_시_대상_계약이_설정된다() {
        Cancellation cancellation = new Cancellation(contract);
        assertEquals(contract, cancellation.getContract());
    }

    // ── selectReason() + validateReasonInput() — Basic Path & A1 ─

    @Test
    public void 일반_해지사유_선택시_validateReasonInput_true() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.selectReason("경제적 사정");
        assertTrue(cancellation.validateReasonInput());
    }

    @Test
    public void 기타_선택_후_상세사유_입력시_validateReasonInput_true() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.selectReason("기타");
        cancellation.enterDetailReason("개인 사정으로 인해 해지합니다.");
        assertTrue(cancellation.validateReasonInput());
    }

    @Test
    public void 기타_선택_후_상세사유_미입력시_validateReasonInput_false() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.selectReason("기타");
        assertFalse(cancellation.validateReasonInput());
    }

    @Test
    public void 기타_선택_후_빈문자열_입력시_validateReasonInput_false() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.selectReason("기타");
        cancellation.enterDetailReason("");
        assertFalse(cancellation.validateReasonInput());
    }

    @Test
    public void 해지사유_미선택시_validateReasonInput_false() {
        Cancellation cancellation = new Cancellation(contract);
        assertFalse(cancellation.validateReasonInput());
    }

    // ── agreeToNotice() ───────────────────────────────────────────

    @Test
    public void 초기_유의사항_동의는_false이다() {
        Cancellation cancellation = new Cancellation(contract);
        assertFalse(cancellation.isNoticeAgreed());
    }

    @Test
    public void agreeToNotice_후_동의_상태가_true로_변경된다() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.agreeToNotice();
        assertTrue(cancellation.isNoticeAgreed());
    }

    // ── authenticate() — step 5 ───────────────────────────────────

    @Test
    public void authenticate_본인인증_성공시_true를_반환한다() {
        Cancellation cancellation = new Cancellation(contract);
        assertTrue(cancellation.authenticate());
    }

    @Test
    public void authenticate_후_authResult가_true이다() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.authenticate();
        assertTrue(cancellation.isAuthResult());
    }

    // ── calculateExpectedRefund() — step 6 ───────────────────────

    @Test
    public void calculateExpectedRefund_월보험료_x_12를_반환한다() {
        Cancellation cancellation = new Cancellation(contract);
        long refund = cancellation.calculateExpectedRefund();
        assertEquals(100000L * 12, refund);
    }

    @Test
    public void calculateExpectedRefund_후_expectedRefund가_설정된다() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.calculateExpectedRefund();
        assertEquals(1200000L, cancellation.getExpectedRefund());
    }

    // ── submit() — step 7~8: 해약하기 ────────────────────────────

    @Test
    public void submit_후_상태가_완료로_변경된다() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.selectReason("경제적 사정");
        cancellation.agreeToNotice();
        cancellation.authenticate();
        cancellation.submit();
        assertEquals("완료", cancellation.getStatus());
    }

    @Test
    public void submit_후_해지일시가_설정된다() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.submit();
        assertNotNull(cancellation.getCanceledAt());
    }

    // ── confirm() — submit()과 동일 동작 ─────────────────────────

    @Test
    public void confirm_후_상태가_완료로_변경된다() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.confirm();
        assertEquals("완료", cancellation.getStatus());
    }

    @Test
    public void confirm_후_해지일시가_설정된다() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.confirm();
        assertNotNull(cancellation.getCanceledAt());
    }

    // ── handleSubmitError() — E2: 신청 처리 오류 ─────────────────

    @Test
    public void handleSubmitError_후_상태가_실패로_변경된다() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.handleSubmitError();
        assertEquals("실패", cancellation.getStatus());
    }

    // ── cancel() — A2: 중간 취소 ─────────────────────────────────

    @Test
    public void cancel_후_상태가_취소로_변경된다() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.cancel();
        assertEquals("취소", cancellation.getStatus());
    }

    @Test
    public void cancel_후_해지일시는_설정되지_않는다() {
        Cancellation cancellation = new Cancellation(contract);
        cancellation.cancel();
        assertNull(cancellation.getCanceledAt());
    }
}