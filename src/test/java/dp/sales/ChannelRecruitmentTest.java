package dp.sales;

import dp.enums.ChannelType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ChannelRecruitment 단위 테스트
 *
 * 검증 대상:
 * - validateRequired(): 필수 항목 검사
 * - save(): 모집번호, 등록일시 설정
 */
public class ChannelRecruitmentTest {

    @Test
    public void validateRequired_모든_필수값_입력시_true() {
        ChannelRecruitment recruitment = new ChannelRecruitment();
        recruitment.setChannelType(ChannelType.DESIGNER);
        recruitment.setRecruitCount(5);
        recruitment.setLocalStartDate(LocalDate.of(2025, 1, 1));
        recruitment.setLocalEndDate(LocalDate.of(2025, 3, 31));
        assertTrue(recruitment.validateRequired());
    }

    @Test
    public void validateRequired_채널유형_누락시_false() {
        ChannelRecruitment recruitment = new ChannelRecruitment();
        recruitment.setRecruitCount(5);
        recruitment.setLocalStartDate(LocalDate.of(2025, 1, 1));
        recruitment.setLocalEndDate(LocalDate.of(2025, 3, 31));
        assertFalse(recruitment.validateRequired());
    }

    @Test
    public void validateRequired_모집인원_0이하면_false() {
        ChannelRecruitment recruitment = new ChannelRecruitment();
        recruitment.setChannelType(ChannelType.AGENCY);
        recruitment.setRecruitCount(0);
        recruitment.setLocalStartDate(LocalDate.now());
        recruitment.setLocalEndDate(LocalDate.now().plusDays(30));
        assertFalse(recruitment.validateRequired());
    }

    @Test
    public void validateRequired_모집인원_음수이면_false() {
        ChannelRecruitment recruitment = new ChannelRecruitment();
        recruitment.setChannelType(ChannelType.DESIGNER);
        recruitment.setRecruitCount(-1);
        recruitment.setLocalStartDate(LocalDate.now());
        recruitment.setLocalEndDate(LocalDate.now().plusDays(30));
        assertFalse(recruitment.validateRequired());
    }

    @Test
    public void validateRequired_시작일_누락시_false() {
        ChannelRecruitment recruitment = new ChannelRecruitment();
        recruitment.setChannelType(ChannelType.DESIGNER);
        recruitment.setRecruitCount(3);
        recruitment.setLocalEndDate(LocalDate.now().plusDays(30));
        assertFalse(recruitment.validateRequired());
    }

    @Test
    public void validateRequired_종료일_누락시_false() {
        ChannelRecruitment recruitment = new ChannelRecruitment();
        recruitment.setChannelType(ChannelType.DESIGNER);
        recruitment.setRecruitCount(3);
        recruitment.setLocalStartDate(LocalDate.now());
        assertFalse(recruitment.validateRequired());
    }

    @Test
    public void save_후_모집번호가_부여된다() {
        ChannelRecruitment recruitment = new ChannelRecruitment();
        recruitment.save();
        assertNotNull(recruitment.getRecruitmentNo());
        assertTrue(recruitment.getRecruitmentNo().startsWith("RC-"));
    }

    @Test
    public void save_후_등록일시가_설정된다() {
        ChannelRecruitment recruitment = new ChannelRecruitment();
        recruitment.save();
        assertNotNull(recruitment.getLocalRegisteredAt());
    }

    @Test
    public void 조건_선택사항_설정이_반영된다() {
        ChannelRecruitment recruitment = new ChannelRecruitment();
        recruitment.setCondition("경력 3년 이상, 생명보험 자격증 소지자");
        assertEquals("경력 3년 이상, 생명보험 자격증 소지자", recruitment.getCondition());
    }
}