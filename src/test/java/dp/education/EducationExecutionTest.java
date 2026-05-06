package dp.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * EducationExecution 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 완료번호 자동 부여
 * - loadAttendanceList(): 출석 명단 로드
 * - markAttendance(): 출석 체크
 * - calculateAttendanceCount(): 출석 인원 계산
 * - complete(): 완료 처리
 */
public class EducationExecutionTest {

    private EducationPreparation preparation;

    @BeforeEach
    public void setUp() {
        preparation = new EducationPreparation();
        preparation.enterPreparationInfo("명지대학교", "김강사", "");
        preparation.addAttendee("홍길동");
        preparation.addAttendee("김철수");
        preparation.addAttendee("이영희");
        preparation.save();
    }

    @Test
    public void 생성_시_완료번호가_부여된다() {
        EducationExecution execution = new EducationExecution(preparation);
        assertTrue(execution.getCompletionNumber() > 0);
    }

    @Test
    public void 출석명단_로드가_정상_동작한다() {
        EducationExecution execution = new EducationExecution(preparation);
        List<Attendance> list = execution.loadAttendanceList();

        assertEquals(3, list.size());
        assertEquals("홍길동", list.get(0).getAttendeeName());
    }

    @Test
    public void 출석체크가_정상_반영된다() {
        EducationExecution execution = new EducationExecution(preparation);
        execution.markAttendance("홍길동", true);
        execution.markAttendance("김철수", false);

        List<Attendance> list = execution.loadAttendanceList();
        assertTrue(list.get(0).isAttended());
        assertFalse(list.get(1).isAttended());
    }

    @Test
    public void 출석인원_계산이_정상_동작한다() {
        EducationExecution execution = new EducationExecution(preparation);
        execution.markAttendance("홍길동", true);
        execution.markAttendance("김철수", true);
        execution.markAttendance("이영희", false);

        int count = execution.calculateAttendanceCount();
        assertEquals(2, count);
        assertEquals(3, execution.getTotalCount());
    }

    @Test
    public void complete_후_완료일시가_설정된다() {
        EducationExecution execution = new EducationExecution(preparation);
        execution.complete();
        assertNotNull(execution.getCompletedAt());
    }

    @Test
    public void 메모_설정이_정상_반영된다() {
        EducationExecution execution = new EducationExecution(preparation);
        execution.setMemo("교육 순조롭게 진행됨");
        assertEquals("교육 순조롭게 진행됨", execution.getMemo());
    }

    @Test
    public void 출석없으면_출석인원_0() {
        EducationExecution execution = new EducationExecution(preparation);
        int count = execution.calculateAttendanceCount();
        assertEquals(0, count);
    }
}