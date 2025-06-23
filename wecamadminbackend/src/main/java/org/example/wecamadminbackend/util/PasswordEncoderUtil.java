package org.example.wecamadminbackend.util;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {

    public static void main(String[] args) {
        String rawPassword = "admin1234";  // 여기에 원하는 비밀번호 입력!
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println(" Encoded Password: " + encodedPassword);
    }
}
