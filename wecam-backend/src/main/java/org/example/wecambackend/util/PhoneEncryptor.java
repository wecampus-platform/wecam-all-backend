package org.example.wecambackend.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PhoneEncryptor {

    private final StandardPBEStringEncryptor encryptor;

    public PhoneEncryptor(@Value("${phone.encrypt-key}") String key) {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(key);
        encryptor.setAlgorithm("PBEWithMD5AndDES");
    }

    public String encrypt(String plainText) {
        return encryptor.encrypt(plainText);
    }

    public String decrypt(String encryptedText) {
        return encryptor.decrypt(encryptedText);
    }
}
