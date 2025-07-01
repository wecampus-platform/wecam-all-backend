package org.example.wecambackend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PhoneEncryptorTest {

    private PhoneEncryptor phoneEncryptor;

    @BeforeEach
    void setup() {
        // 충분히 강력한 32자 이상 키 설정 (테스트용, 실제 서비스 키는 환경변수)
        String testKey = "ThisIsA32CharLongEncryptionKey!!!";
        phoneEncryptor = new PhoneEncryptor(testKey);
    }

    @Test
    @DisplayName("전화번호 암호화 후 복호화하면 원문과 일치해야 한다")
    void 전화번호_암호화_복호화_테스트() {
        String originalPhone = "01012345678";

        String encrypted = phoneEncryptor.encrypt(originalPhone);
        assertNotNull(encrypted);
        assertNotEquals(originalPhone, encrypted);

        String decrypted = phoneEncryptor.decrypt(encrypted);
        assertEquals(originalPhone, decrypted);
    }

    @Test
    @DisplayName("동일한 전화번호 입력 시 항상 동일한 암호문을 생성해야 한다")
    void 동일한_입력_동일한_암호문_생성_테스트() {
        String phone = "01012345678";

        String encrypted1 = phoneEncryptor.encrypt(phone);
        String encrypted2 = phoneEncryptor.encrypt(phone);

        assertEquals(encrypted1, encrypted2, "동일 입력은 항상 동일한 암호문 생성");
    }

    @Test
    @DisplayName("서로 다른 전화번호는 서로 다른 암호문을 생성해야 한다")
    void 다른_입력_다른_암호문_생성_테스트() {
        String phone1 = "01012345678";
        String phone2 = "01087654321";

        String encrypted1 = phoneEncryptor.encrypt(phone1);
        String encrypted2 = phoneEncryptor.encrypt(phone2);

        assertNotEquals(encrypted1, encrypted2, "다른 입력은 다른 암호문 생성");
    }
}
