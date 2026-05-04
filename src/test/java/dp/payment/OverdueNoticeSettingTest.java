package com.insurance.test.payment;

import com.insurance.payment.OverdueNoticeSetting;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * OverdueNoticeSetting 단위 테스트
 *
 * 검증 대상:
 * - 생성 시 기본값: enabled=false
 * - 활성화 토글, 발송일·템플릿 설정
 * - 미리보기/저장
 */
public class OverdueNoticeSettingTest {

    @Test
    public void 생성_시_기본값_false() {
        OverdueNoticeSetting setting = new OverdueNoticeSetting();

        assertFalse(setting.isEnabled());
        assertEquals(0, setting.getDaysAfterDue());
        assertNull(setting.getMessageTemplate());
    }

    @Test
    public void 활성화_토글() {
        OverdueNoticeSetting setting = new OverdueNoticeSetting();
        setting.toggleEnabled(true);
        assertTrue(setting.isEnabled());

        setting.toggleEnabled(false);
        assertFalse(setting.isEnabled());
    }

    @Test
    public void 발송_기준일_설정() {
        OverdueNoticeSetting setting = new OverdueNoticeSetting();
        setting.setDaysAfterDue(7);

        assertEquals(7, setting.getDaysAfterDue());
    }

    @Test
    public void 메시지_템플릿_설정_및_미리보기() {
        OverdueNoticeSetting setting = new OverdueNoticeSetting();
        setting.setMessageTemplate("보험료 미납 안내");

        assertEquals("보험료 미납 안내", setting.previewMessage());
    }

    @Test
    public void 저장_시_시각_갱신() {
        OverdueNoticeSetting setting = new OverdueNoticeSetting();
        setting.toggleEnabled(true);
        setting.setDaysAfterDue(5);
        setting.setMessageTemplate("미납 알림");
        setting.save();

        assertNotNull(setting.getSavedAt());
    }
}
