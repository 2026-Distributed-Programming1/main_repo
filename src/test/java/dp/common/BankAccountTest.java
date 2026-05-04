package com.insurance.test.common;

import com.insurance.common.BankAccount;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * BankAccount 단위 테스트
 *
 * 검증 대상:
 * - 빈 상태 생성 직후 verified=false
 * - 모든 필드 입력 후 verify() 성공
 * - 일부 필드 누락 시 verify() 실패 (E1 시뮬레이션)
 */
public class BankAccountTest {

    private BankAccount account;

    @Before
    public void setUp() {
        account = new BankAccount();
    }

    @Test
    public void 생성_직후_미인증_상태이다() {
        assertFalse(account.isVerified());
        assertNull(account.getBankName());
        assertNull(account.getAccountNo());
        assertNull(account.getAccountHolder());
    }

    @Test
    public void 입력_후_필드값이_정상_저장된다() {
        account.enter("국민은행", "123-456-789012", "홍길동");
        assertEquals("국민은행", account.getBankName());
        assertEquals("123-456-789012", account.getAccountNo());
        assertEquals("홍길동", account.getAccountHolder());
    }

    @Test
    public void 모든_필드_입력_후_verify_성공() {
        account.enter("신한은행", "987-654-321098", "김고객");
        boolean result = account.verify();
        assertTrue(result);
        assertTrue(account.isVerified());
    }

    @Test
    public void 빈_계좌_E1_verify_실패() {
        // E1: 본인 명의가 아니거나 정보가 잘못된 경우 시뮬레이션
        boolean result = account.verify();
        assertFalse(result);
        assertFalse(account.isVerified());
    }

    @Test
    public void 일부_필드_누락_시_verify_실패() {
        account.enter("우리은행", null, "이고객");
        assertFalse(account.verify());
    }
}
