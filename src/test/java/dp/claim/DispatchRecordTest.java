package com.insurance.test.claim;

import com.insurance.actor.Customer;
import com.insurance.claim.AccidentReport;
import com.insurance.claim.Dispatch;
import com.insurance.claim.DispatchRecord;
import com.insurance.common.Attachment;
import com.insurance.enums.DispatchRecordStatus;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * DispatchRecord 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 기록 ID 자동 부여, 초기 상태 DRAFT, 빈 사진 목록
 * - 사진 업로드/삭제
 * - 필수 항목 검증 (E1: 사진/특이사항 누락)
 * - 전송 후 상태 TRANSMITTED, 시각 자동 설정
 */
public class DispatchRecordTest {

    private Dispatch dispatch;

    @Before
    public void setUp() {
        Customer customer = new Customer("테스트고객", "900101-1234567", "010-0000-0000", "test@test.com");
        AccidentReport accident = new AccidentReport(customer);
        dispatch = new Dispatch(accident);
    }

    @Test
    public void 생성_시_기록ID와_초기상태가_설정된다() {
        DispatchRecord record = new DispatchRecord(dispatch);

        assertNotNull(record.getRecordId());
        assertTrue(record.getRecordId().startsWith("DRC"));
        assertEquals(DispatchRecordStatus.DRAFT, record.getStatus());
        assertNotNull(record.getPhotos());
        assertTrue(record.getPhotos().isEmpty());
    }

    @Test
    public void 사진_업로드_시_목록에_추가된다() {
        DispatchRecord record = new DispatchRecord(dispatch);
        Attachment photo = new Attachment(new File("front.jpg"));
        record.uploadPhoto("전경", photo);

        assertEquals(1, record.getPhotos().size());
        assertTrue(record.getPhotos().contains(photo));
    }

    @Test
    public void 사진_삭제_시_목록에서_제거된다() {
        DispatchRecord record = new DispatchRecord(dispatch);
        Attachment photo = new Attachment(new File("front.jpg"));
        record.uploadPhoto("전경", photo);
        record.removePhoto(photo);

        assertTrue(record.getPhotos().isEmpty());
    }

    @Test
    public void 경찰_견인_플래그_설정() {
        DispatchRecord record = new DispatchRecord(dispatch);
        record.setPoliceRequired(true);
        record.setTowingRequired(false);

        assertTrue(record.isPoliceRequired());
        assertFalse(record.isTowingRequired());
    }

    @Test
    public void E1_사진과_특이사항_누락시_validateRequired_실패() {
        DispatchRecord record = new DispatchRecord(dispatch);
        // 아무 입력도 하지 않음
        assertFalse(record.validateRequired());
    }

    @Test
    public void E1_사진은_있어도_특이사항_누락시_validateRequired_실패() {
        DispatchRecord record = new DispatchRecord(dispatch);
        record.uploadPhoto("전경", new Attachment(new File("front.jpg")));
        // notes 미입력
        assertFalse(record.validateRequired());
    }

    @Test
    public void 모든_필수항목_입력시_validateRequired_통과() {
        DispatchRecord record = createCompleteRecord();
        assertTrue(record.validateRequired());
    }

    @Test
    public void 전송_후_상태와_시각이_갱신된다() {
        DispatchRecord record = createCompleteRecord();
        record.transmit();

        assertEquals(DispatchRecordStatus.TRANSMITTED, record.getStatus());
        assertNotNull(record.getTransmittedAt());
    }

    @Test
    public void E1_필수항목_누락시_전송_안됨() {
        DispatchRecord record = new DispatchRecord(dispatch);
        record.transmit();

        // 상태가 TRANSMITTED로 바뀌지 않아야 함
        assertEquals(DispatchRecordStatus.DRAFT, record.getStatus());
        assertNull(record.getTransmittedAt());
    }

    private DispatchRecord createCompleteRecord() {
        DispatchRecord record = new DispatchRecord(dispatch);
        record.uploadPhoto("전경", new Attachment(new File("front.jpg")));
        record.enterNotes("후방 추돌 사고");
        return record;
    }
}
