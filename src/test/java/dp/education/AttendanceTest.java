package dp.education;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Attendance 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 대상자명 설정, 초기 출석여부 false
 * - mark(): 출석 체크
 */
public class AttendanceTest {

    @Test
    public void 생성_시_대상자명이_설정되고_출석여부는_false다() {
        Attendance attendance = new Attendance("홍길동");

        assertEquals("홍길동", attendance.getAttendeeName());
        assertFalse(attendance.isAttended());
    }

    @Test
    public void 출석체크_true_정상_반영된다() {
        Attendance attendance = new Attendance("홍길동");
        attendance.mark(true);
        assertTrue(attendance.isAttended());
    }

    @Test
    public void 출석체크_false_정상_반영된다() {
        Attendance attendance = new Attendance("홍길동");
        attendance.mark(true);
        attendance.mark(false);
        assertFalse(attendance.isAttended());
    }
}