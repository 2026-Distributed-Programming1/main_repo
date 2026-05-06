package dp.consultation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Proposal 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 제안번호 자동 부여
 * - selectProduct(): 보험상품 선택
 * - send(): 제안서 발송 및 발송일시 설정
 */
public class ProposalTest {

    @Test
    public void 생성_시_제안번호가_부여된다() {
        Proposal proposal = new Proposal();
        assertTrue(proposal.getProposalId() > 0);
    }

    @Test
    public void 상품선택이_정상_반영된다() {
        Proposal proposal = new Proposal();
        InsuranceProduct product = new InsuranceProduct(
                "실손의료보험", "건강", 50000L, "의료비 전액 보장", "치과 제외");

        proposal.selectProduct(product);

        assertNotNull(proposal.getInsuranceProduct());
        assertEquals("실손의료보험", proposal.getInsuranceProduct().getProductName());
    }

    @Test
    public void send_후_발송일시가_설정된다() {
        Proposal proposal = new Proposal();
        InsuranceProduct product = new InsuranceProduct(
                "실손의료보험", "건강", 50000L, "의료비 전액 보장", "치과 제외");
        proposal.selectProduct(product);
        proposal.send();

        assertNotNull(proposal.getSentAt());
    }

    @Test
    public void 고객명_설정이_정상_반영된다() {
        Proposal proposal = new Proposal();
        proposal.setCustomerName("홍길동");
        assertEquals("홍길동", proposal.getCustomerName());
    }

    @Test
    public void 상품_미선택시_InsuranceProduct가_null이다() {
        Proposal proposal = new Proposal();
        assertNull(proposal.getInsuranceProduct());
    }
}