package com.example.prompt;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "admin1234";

        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("암호화된 비밀번호:");
        System.out.println(encodedPassword);
    }
}
