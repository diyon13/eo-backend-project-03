package com.example.prompt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    // 인증번호 저장
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    // 인증 유효 시간 저장
    private final Map<String, Long> expirationTimes = new ConcurrentHashMap<>();

    // 인증 완료 여부 저장
    private final Map<String, Boolean> verifiedEmails = new ConcurrentHashMap<>();

    // 5분
    private static final long EXPIRATION_TIME = 5 * 60 * 1000;

    // 인증번호 생성 및 이메일 발송
    public String sendVerificationCode(String email) {
        log.info("Sending verification code to : {}", email);

        String code = generateCode();

        // 인증번호 저장
        verificationCodes.put(email, code);
        expirationTimes.put(email, System.currentTimeMillis() + EXPIRATION_TIME);
        verifiedEmails.remove(email); // 재발송 시 인증 초기화

        // 이메일 발송
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[닥트리오] 이메일 인증번호");
            message.setText("인증번호: " + code + "\n\n5분 이내에 입력해주세요.");

            mailSender.send(message);

            log.info("Verification code sent successfully to: {}", email);
            return code;
        } catch (Exception e) {
            log.error("Failed to send email to: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }

    // 인증번호 검증
    public boolean verifyCode(String email, String code) {
        log.info("Verifying code for: {}", email);

        // 인증번호 확인
        String savedCode = verificationCodes.get(email);
        if (savedCode == null) {
            log.warn("No verification code found for: {}", email);
            return false;
        }

        // 유효시간 확인
        Long expirationTime = expirationTimes.get(email);
        if (expirationTime == null || System.currentTimeMillis() > expirationTime) {
            log.warn("Verification code expired for: {}", email);
            verificationCodes.remove(email);
            expirationTimes.remove(email);
            return false;
        }

        // 인증번호 비교
        boolean isValid = savedCode.equals(code);

        if (isValid) {
            log.info("Verification successful for: {}", email);
            verificationCodes.remove(email);
            expirationTimes.remove(email);
            verifiedEmails.put(email, true); // 인증 완료 표시
        } else {
            log.warn("Invalid verification code for: {}", email);
        }

        return isValid;
    }

    // 이메일 인증 완료 여부 확인 (회원가입/비번찾기에서 사용)
    public boolean isVerified(String email) {
        return Boolean.TRUE.equals(verifiedEmails.get(email));
    }

    // 인증 완료 상태 초기화 (회원가입 완료 후 호출)
    public void clearVerified(String email) {
        verifiedEmails.remove(email);
    }

    // 난수 생성
    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

}
