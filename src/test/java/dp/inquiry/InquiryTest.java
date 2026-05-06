package dp.inquiry;

import dp.enums.FaqCategory;
import dp.enums.InquiryStatus;
import dp.enums.InquiryType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Inquiry 단위 테스트
 *
 * 검증 대상:
 * - validateRequired(): 필수 항목 검사 (문의유형, 제목, 내용) — E1 흐름
 * - validateFileSize(): 첨부파일 10MB 이하 검증 — E2 흐름
 * - setTitle(): 제목 설정 시 currentLength 자동 반영
 * - submit(): 문의번호 자동 부여, 접수일시 설정, 상태 PENDING — step 9
 */
public class InquiryTest {

    // ── validateRequired() — E1: 필수 항목 누락 ──────────────────

    @Test
    public void validateRequired_모든_필수값_입력시_true() {
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryType(InquiryType.CLAIM);
        inquiry.setTitle("보험금 청구 관련 문의");
        inquiry.setContent("보험금 청구 절차를 알고 싶습니다.");
        assertTrue(inquiry.validateRequired());
    }

    @Test
    public void validateRequired_문의유형_누락시_false() {
        Inquiry inquiry = new Inquiry();
        inquiry.setTitle("제목");
        inquiry.setContent("내용");
        assertFalse(inquiry.validateRequired());
    }

    @Test
    public void validateRequired_제목_누락시_false() {
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryType(InquiryType.INSURANCE);
        inquiry.setContent("내용");
        assertFalse(inquiry.validateRequired());
    }

    @Test
    public void validateRequired_제목_빈문자열이면_false() {
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryType(InquiryType.OTHER);
        inquiry.setTitle("");
        inquiry.setContent("내용");
        assertFalse(inquiry.validateRequired());
    }

    @Test
    public void validateRequired_내용_누락시_false() {
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryType(InquiryType.CANCELLATION);
        inquiry.setTitle("제목");
        assertFalse(inquiry.validateRequired());
    }

    @Test
    public void validateRequired_내용_빈문자열이면_false() {
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryType(InquiryType.CONTRACT_CHANGE);
        inquiry.setTitle("제목");
        inquiry.setContent("");
        assertFalse(inquiry.validateRequired());
    }

    @Test
    public void validateRequired_제목이_50자_초과면_false() {
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryType(InquiryType.CLAIM);
        inquiry.setTitle("가".repeat(51));
        inquiry.setContent("내용");
        assertFalse(inquiry.validateRequired());
    }

    @Test
    public void validateRequired_제목이_정확히_50자면_true() {
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryType(InquiryType.CLAIM);
        inquiry.setTitle("가".repeat(50));
        inquiry.setContent("내용");
        assertTrue(inquiry.validateRequired());
    }

    @Test
    public void validateRequired_내용이_1000자_초과면_false() {
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryType(InquiryType.CLAIM);
        inquiry.setTitle("제목");
        inquiry.setContent("가".repeat(1001));
        assertFalse(inquiry.validateRequired());
    }

    @Test
    public void validateRequired_내용이_정확히_1000자면_true() {
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryType(InquiryType.CLAIM);
        inquiry.setTitle("제목");
        inquiry.setContent("가".repeat(1000));
        assertTrue(inquiry.validateRequired());
    }

    // ── validateFileSize() — E2: 첨부파일 10MB 초과 ──────────────

    @Test
    public void validateFileSize_첨부파일_없으면_true() {
        Inquiry inquiry = new Inquiry();
        assertTrue(inquiry.validateFileSize());
    }

    @Test
    public void validateFileSize_10MB_이하면_true() {
        Inquiry inquiry = new Inquiry();
        inquiry.setAttachmentFileSize(10L * 1024 * 1024); // 정확히 10MB
        assertTrue(inquiry.validateFileSize());
    }

    @Test
    public void validateFileSize_10MB_초과면_false() {
        Inquiry inquiry = new Inquiry();
        inquiry.setAttachmentFileSize(10L * 1024 * 1024 + 1);
        assertFalse(inquiry.validateFileSize());
    }

    @Test
    public void validateFileSize_1MB면_true() {
        Inquiry inquiry = new Inquiry();
        inquiry.setAttachmentFileSize(1L * 1024 * 1024);
        assertTrue(inquiry.validateFileSize());
    }

    // ── setTitle() — currentLength 자동 반영 ──────────────────────

    @Test
    public void setTitle_후_currentLength가_제목_길이와_일치한다() {
        Inquiry inquiry = new Inquiry();
        inquiry.setTitle("보험금 청구 문의");
        assertEquals("보험금 청구 문의".length(), inquiry.getCurrentLength());
    }

    @Test
    public void setTitle_빈문자열이면_currentLength가_0이다() {
        Inquiry inquiry = new Inquiry();
        inquiry.setTitle("");
        assertEquals(0, inquiry.getCurrentLength());
    }

    @Test
    public void setTitle_null이면_currentLength가_0이다() {
        Inquiry inquiry = new Inquiry();
        inquiry.setTitle(null);
        assertEquals(0, inquiry.getCurrentLength());
    }

    // ── submit() — step 9: 문의 제출 ─────────────────────────────

    @Test
    public void submit_후_문의번호가_부여된다() {
        Inquiry inquiry = new Inquiry();
        inquiry.submit();
        assertNotNull(inquiry.getInquiryNo());
        assertTrue(inquiry.getInquiryNo().startsWith("INQ-"));
    }

    @Test
    public void submit_후_접수일시가_설정된다() {
        Inquiry inquiry = new Inquiry();
        inquiry.submit();
        assertNotNull(inquiry.getReceivedAt());
    }

    @Test
    public void submit_후_상태가_PENDING이다() {
        Inquiry inquiry = new Inquiry();
        inquiry.submit();
        assertEquals(InquiryStatus.PENDING, inquiry.getStatus());
    }

    @Test
    public void submit_전_상태는_null이다() {
        Inquiry inquiry = new Inquiry();
        assertNull(inquiry.getStatus());
    }

    // ── 모든 InquiryType에 대해 validateRequired 통과 ────────────

    @Test
    public void 모든_문의유형에_대해_validateRequired가_통과된다() {
        for (InquiryType type : InquiryType.values()) {
            Inquiry inquiry = new Inquiry();
            inquiry.setInquiryType(type);
            inquiry.setTitle("제목");
            inquiry.setContent("내용");
            assertTrue(inquiry.validateRequired(), type + " 유형이어야 통과해야 합니다");
        }
    }

    // ── FAQ 카테고리 설정 — A1 흐름 ──────────────────────────────
    // getFaqCategory() getter가 없으므로 setter 호출 시 예외 없음만 확인

    @Test
    public void FAQ_카테고리_설정_시_예외가_발생하지_않는다() {
        Inquiry inquiry = new Inquiry();
        assertDoesNotThrow(() -> inquiry.setFaqCategory(FaqCategory.CLAIM));
    }
}