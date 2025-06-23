package org.example.wecambackend.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class PhoneEncryptorTest {

    public static void main(String[] args) {
        String key = "your-secret-key"; // 🔐 application.yml의 phone.encrypt-key
        String phone = "010-1234-5678"; // 테스트할 전화번호

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(key);
        encryptor.setAlgorithm("PBEWithMD5AndDES");

        String encrypted = encryptor.encrypt(phone);
        String decrypted = encryptor.decrypt(encrypted);

        System.out.println("📞 원본 번호: " + phone);
        System.out.println("🔐 암호화된 값: " + encrypted);
        System.out.println("🔓 복호화 테스트: " + decrypted);
    }
}
