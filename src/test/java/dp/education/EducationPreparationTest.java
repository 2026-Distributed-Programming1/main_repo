package dp.education;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * EducationPreparation 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 등록번호 자동 부여, 출석목록 초기화
 * - enterPreparationInfo(): 필드 정상 반영
 * - validateRequiredFields(): 필수항목 검증 (E1)
 * - addAttendee(): 출석 대상자 추가
 * - save(): 등록일시 설정
 */
public class EducationPreparationTest {

    @Test
    public void 생성_시_등록번호와_출석목록이_초기화된다() {
        EducationPreparation prep = new EducationPreparation();

        assertTrue(prep.getSetupNumber() > 0);
        assertNotNull(prep.getAttendanceList());
        assertTrue(prep.getAttendanceList().isEmpty());
    }

    @Test
    public void 제반정보_입력이_정상_반영된다() {
        EducationPreparation prep = new EducationPreparation();
        prep.enterPreparationInfo("명지대학교", "김강사", "기타 없음");

        assertEquals("명지대학교", prep.getLocation());
        assertEquals("김강사", prep.getInstructorName());
    }

    @Test
    public void 필수항목_모두_입력시_검증_통과() {
        EducationPreparation prep = new EducationPreparation();
        prep.enterPreparationInfo("명지대학교", "김강사", "");
        assertTrue(prep.validateRequiredFields());
    }

    @Test
    public void 교육장소_미입력시_검증_실패() {
        EducationPreparation prep = new EducationPreparation();
        prep.enterPreparationInfo("", "김강사", "");
        assertFalse(prep.validateRequiredFields());
    }

    @Test
    public void 강사명_미입력시_검증_실패() {
        EducationPreparation prep = new EducationPreparation();
        prep.enterPreparationInfo("명지대학교", "", "");
        assertFalse(prep.validateRequiredFields());
    }

    @Test
    public void 출석대상자_추가가_정상_반영된다() {
        EducationPreparation prep = new EducationPreparation();
        prep.addAttendee("홍길동");
        prep.addAttendee("김철수");

        assertEquals(2, prep.getAttendanceList().size());
        assertEquals("홍길동", prep.getAttendanceList().get(0).getAttendeeName());
        assertEquals("김철수", prep.getAttendanceList().get(1).getAttendeeName());
    }

    @Test
    public void save_후_등록일시가_설정된다() {
        EducationPreparation prep = new EducationPreparation();
        prep.enterPreparationInfo("명지대학교", "김강사", "");
        prep.save();

        assertNotNull(prep.getRegisteredAt());
    }

    @Test
    public void 교재현황_설정이_정상_반영된다() {
        EducationPreparation prep = new EducationPreparation();
        prep.setTextbookStatus("준비완료");
        assertEquals("준비완료", prep.getTextbookStatus());
    }
}