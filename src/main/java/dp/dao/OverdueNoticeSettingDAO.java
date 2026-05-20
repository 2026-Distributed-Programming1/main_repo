package dp.dao;

import dp.db.DBA;
import dp.payment.OverdueNoticeSetting;

public class OverdueNoticeSettingDAO {

    // DB 컬럼명(max_overdue_count → daysAfterDue, notice_method → messageTemplate)이
    // 도메인 필드명과 다르지만, save/find 양방향이 동일하게 매핑하므로 데이터 왕복은 정상이다.
    public static void save(OverdueNoticeSetting s) {
        DBA.executeUpdate(
            "INSERT INTO overdue_notice_settings (id, max_overdue_count, notice_method, auto_cancel_enabled)"
            + " VALUES (1,?,?,?)"
            + " ON DUPLICATE KEY UPDATE max_overdue_count=VALUES(max_overdue_count),"
            + " notice_method=VALUES(notice_method), auto_cancel_enabled=VALUES(auto_cancel_enabled)",
            s.getDaysAfterDue(),
            s.getMessageTemplate(),
            s.isEnabled());
    }

    public static OverdueNoticeSetting find() {
        return DBA.queryOne(
            "SELECT max_overdue_count, notice_method, auto_cancel_enabled"
            + " FROM overdue_notice_settings WHERE id=1",
            rs -> {
                OverdueNoticeSetting s = new OverdueNoticeSetting();
                s.setDaysAfterDue(rs.getInt("max_overdue_count"));
                s.setMessageTemplate(rs.getString("notice_method"));
                s.toggleEnabled(rs.getBoolean("auto_cancel_enabled"));
                return s;
            });
    }
}