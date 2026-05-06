package dp.contract;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ContractStatistics 단위 테스트
 *
 * 검증 대상:
 * - 생성자: monthlyRetentionData 초기화
 * - validateDateRange(): 연월 범위 유효성 검사 (E1 흐름)
 * - generateFileName(): 파일명 자동 생성 규칙
 * - exportToExcel(): 파일명 설정 및 File 객체 반환 (step 5~6)
 */
public class ContractStatisticsTest {

    @Test
    public void 생성_시_monthlyRetentionData가_초기화된다() {
        ContractStatistics stats = new ContractStatistics();
        assertNotNull(stats.getFilterStartMonth());  // null 이어도 NPE 없이 동작
    }

    // ── validateDateRange() — E1: 종료 연월이 시작 연월보다 앞선 경우 ──

    @Test
    public void validateDateRange_시작연월이_종료연월보다_이전이면_true() {
        ContractStatistics stats = new ContractStatistics();
        stats.setFilterStartMonth(YearMonth.of(2025, 1));
        stats.setFilterEndMonth(YearMonth.of(2025, 12));
        assertTrue(stats.validateDateRange());
    }

    @Test
    public void validateDateRange_시작연월과_종료연월이_같으면_true() {
        ContractStatistics stats = new ContractStatistics();
        stats.setFilterStartMonth(YearMonth.of(2025, 6));
        stats.setFilterEndMonth(YearMonth.of(2025, 6));
        assertTrue(stats.validateDateRange());
    }

    @Test
    public void validateDateRange_종료연월이_시작연월보다_이전이면_false() {
        ContractStatistics stats = new ContractStatistics();
        stats.setFilterStartMonth(YearMonth.of(2025, 12));
        stats.setFilterEndMonth(YearMonth.of(2025, 1));
        assertFalse(stats.validateDateRange());
    }

    @Test
    public void validateDateRange_시작연월이_null이면_false() {
        ContractStatistics stats = new ContractStatistics();
        stats.setFilterEndMonth(YearMonth.of(2025, 6));
        assertFalse(stats.validateDateRange());
    }

    @Test
    public void validateDateRange_종료연월이_null이면_false() {
        ContractStatistics stats = new ContractStatistics();
        stats.setFilterStartMonth(YearMonth.of(2025, 1));
        assertFalse(stats.validateDateRange());
    }

    @Test
    public void validateDateRange_둘_다_null이면_false() {
        ContractStatistics stats = new ContractStatistics();
        assertFalse(stats.validateDateRange());
    }

    // ── generateFileName() — step 6: 파일명 규칙 ─────────────────

    @Test
    public void generateFileName_계약번호_포함된_파일명을_반환한다() {
        ContractStatistics stats = new ContractStatistics();
        stats.setContractNo("CON00001");
        String fileName = stats.generateFileName();
        assertTrue(fileName.startsWith("계약통계_CON00001_"));
        assertTrue(fileName.endsWith(".xlsx"));
    }

    @Test
    public void generateFileName_오늘_날짜가_포함된다() {
        ContractStatistics stats = new ContractStatistics();
        stats.setContractNo("CON00002");
        String fileName = stats.generateFileName();
        String today = java.time.LocalDate.now().toString();
        assertTrue(fileName.contains(today));
    }

    // ── exportToExcel() — step 5~6: 엑셀 다운로드 ────────────────

    @Test
    public void exportToExcel_File_객체를_반환한다() {
        ContractStatistics stats = new ContractStatistics();
        stats.setContractNo("CON00003");
        File file = stats.exportToExcel();
        assertNotNull(file);
    }

    @Test
    public void exportToExcel_후_fileName이_설정된다() {
        ContractStatistics stats = new ContractStatistics();
        stats.setContractNo("CON00004");
        stats.exportToExcel();
        assertNotNull(stats.getFileName());
        assertTrue(stats.getFileName().endsWith(".xlsx"));
    }

    @Test
    public void exportToExcel_반환된_파일명이_generateFileName과_일치한다() {
        ContractStatistics stats = new ContractStatistics();
        stats.setContractNo("CON00005");
        String expected = stats.generateFileName();
        stats.exportToExcel();
        assertEquals(expected, stats.getFileName());
    }

    // ── 계약자명 설정 ─────────────────────────────────────────────

    @Test
    public void 계약자명_설정이_반영된다() {
        ContractStatistics stats = new ContractStatistics();
        stats.setContractorName("홍길동");
        assertEquals("홍길동", stats.getContractorName());
    }
}