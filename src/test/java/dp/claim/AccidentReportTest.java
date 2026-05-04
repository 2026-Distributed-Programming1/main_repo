package java.dp.claim;

import com.insurance.actor.Customer;
import com.insurance.claim.AccidentReport;
import com.insurance.claim.Dispatch;
import com.insurance.enums.AccidentReportStatus;
import com.insurance.enums.AccidentType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * AccidentReport 단위 테스트
 *
 * 검증 대상:
 * - 생성자: 접수번호 자동 부여, 접수일시 자동 설정, 초기 상태 DRAFT
 * - 입력 메서드들이 필드를 올바르게 채움
 * - 필수 항목 검증 (E1: 미동의 시 등)
 * - A2: 현장출동 미신청 시 dispatch 객체 생성 안 됨
 */
public class AccidentReportTest {

    private Customer customer;

    @Before
    public void setUp() {
        customer = new Customer("테스트고객", "900101-1234567", "010-0000-0000", "test@test.com");
    }

    @Test
    public void 생성_시_접수번호와_초기상태가_설정된다() {
        AccidentReport report = new AccidentReport(customer);

        assertNotNull(report.getReportNo());
        assertTrue(report.getReportNo().startsWith("ACC"));
        assertNotNull(report.getReportedAt());
        assertEquals(AccidentReportStatus.DRAFT, report.getStatus());
        assertFalse(report.isAgreedTerms());
    }

    @Test
    public void 차량정보_입력이_정상_반영된다() {
        AccidentReport report = new AccidentReport(customer);
        report.enterVehicleInfo("12가3456", "홍길동", "010-1111-2222");

        assertEquals("12가3456", report.getVehicleNo());
        assertEquals("홍길동", report.getOwnerName());
        assertEquals("010-1111-2222", report.getContact());
    }

    @Test
    public void 사고유형_선택이_정상_반영된다() {
        AccidentReport report = new AccidentReport(customer);
        report.selectAccidentType(AccidentType.OBJECT, "차량 파손");

        assertEquals(AccidentType.OBJECT, report.getAccidentType());
        assertEquals("차량 파손", report.getDamageType());
    }

    @Test
    public void 필수항목_모두_입력시_검증_통과() {
        AccidentReport report = createCompleteReport();
        assertTrue(report.validateRequiredFields());
    }

    @Test
    public void 약관_미동의시_필수검증_실패() {
        AccidentReport report = new AccidentReport(customer);
        report.enterVehicleInfo("12가3456", "홍길동", "010-1111-2222");
        report.selectAccidentType(AccidentType.OBJECT, "파손");
        report.enterLocation("강남구");
        // agreeTerms() 호출하지 않음
        assertFalse(report.validateRequiredFields());
    }

    @Test
    public void 차량번호_미입력시_필수검증_실패() {
        AccidentReport report = new AccidentReport(customer);
        report.selectAccidentType(AccidentType.OBJECT, "파손");
        report.enterLocation("강남구");
        report.agreeTerms();
        // 차량번호 미입력
        assertFalse(report.validateRequiredFields());
    }

    @Test
    public void 가입내역_확인_E1_고객_있으면_통과() {
        AccidentReport report = new AccidentReport(customer);
        assertTrue(report.verifyContract());
    }

    @Test
    public void 접수_처리_후_상태_변경된다() {
        AccidentReport report = createCompleteReport();
        report.receive();
        assertEquals(AccidentReportStatus.RECEIVED, report.getStatus());
    }

    @Test
    public void 현장출동_신청_시_Dispatch_객체_생성() {
        AccidentReport report = createCompleteReport();
        report.setDispatchOption(true);
        report.receive();

        Dispatch dispatch = report.requestDispatch();
        assertNotNull(dispatch);
        assertEquals(report, dispatch.getAccident());
    }

    @Test
    public void A2_현장출동_미신청시_Dispatch_생성안됨() {
        AccidentReport report = createCompleteReport();
        report.setDispatchOption(false);
        report.receive();

        Dispatch dispatch = report.requestDispatch();
        assertNull(dispatch);
    }

    @Test
    public void A2_사고접수만_진행_후_취소() {
        AccidentReport report = createCompleteReport();
        report.cancel();
        assertEquals(AccidentReportStatus.CANCELED, report.getStatus());
    }

    /** 완전히 입력이 끝난 사고접수 객체 생성 헬퍼 */
    private AccidentReport createCompleteReport() {
        AccidentReport report = new AccidentReport(customer);
        report.enterVehicleInfo("12가3456", "홍길동", "010-1111-2222");
        report.selectAccidentType(AccidentType.OBJECT, "파손");
        report.enterLocation("서울시 강남구");
        report.agreeTerms();
        return report;
    }
}
