package dp.inquiry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CustomerCenterPage 단위 테스트
 *
 * 검증 대상:
 * - switchTab(): 탭 전환 시 예외 없이 동작 — step 3, A1, A2 흐름
 *
 * 보고서 기준: activeTab 속성과 switchTab() 메서드만 존재하는 단순 클래스.
 * switchTab()은 스텁이므로 호출 시 예외 없음을 확인한다.
 */
public class CustomerCenterPageTest {

    @Test
    public void 기본_생성_시_예외가_발생하지_않는다() {
        assertDoesNotThrow(() -> new CustomerCenterPage());
    }

    @Test
    public void switchTab_호출_시_예외가_발생하지_않는다() {
        CustomerCenterPage page = new CustomerCenterPage();
        assertDoesNotThrow(page::switchTab);
    }
}