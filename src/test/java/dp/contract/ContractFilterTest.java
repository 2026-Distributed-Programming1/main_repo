package dp.contract;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ContractFilter 단위 테스트
 *
 * 검증 대상:
 * - 속성 설정: 모든 필터 항목은 선택 사항 (필수 없음)
 * - apply() / reset(): 스텁 메서드 호출 시 예외 없이 동작
 *
 * 보고서 기준: 모든 항목이 선택 사항이므로 필수값 검증 없음
 * ContractFilter는 Contract.search()에 조건을 전달하는 역할
 */
public class ContractFilterTest {

    @Test
    public void 기본_생성_시_예외가_발생하지_않는다() {
        assertDoesNotThrow(() -> new ContractFilter());
    }

    @Test
    public void 모든_필터_항목은_선택사항이므로_빈_상태로도_apply_가능하다() {
        ContractFilter filter = new ContractFilter();
        assertDoesNotThrow(filter::apply);
    }

    @Test
    public void reset_호출_시_예외가_발생하지_않는다() {
        ContractFilter filter = new ContractFilter();
        assertDoesNotThrow(filter::reset);
    }
}