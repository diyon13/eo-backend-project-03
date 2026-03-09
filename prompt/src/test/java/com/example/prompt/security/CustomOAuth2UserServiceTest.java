package com.example.prompt.security;

import com.example.prompt.domain.PlanEntity;
import com.example.prompt.domain.UserEntity;
import com.example.prompt.repository.PlanRepository;
import com.example.prompt.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
public class CustomOAuth2UserServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    public void setUp() {
        PlanEntity plan = planRepository.save(PlanEntity.builder()
                .planName("NORMAL")
                .tokenLimit(10000)
                .aiUse(0)
                .price(0)
                .build());

        // OAuth2 테스트용 유저 저장 (구글 로그인으로 가입된 유저 시뮬레이션)
        userRepository.save(UserEntity.builder()
                .userid("oauth2_user")
                .username("OAuth2유저")
                .email("oauth2_test@gmail.com")
                .password(passwordEncoder.encode("password123!"))
                .plan(plan)
                .provider("google")
                .providerId("google_test_id_12345")
                .build());

        log.info("setUp - NORMAL 플랜 및 OAuth2 유저 저장 완료");
    }

    // OAuth2 로그인 엔드포인트 접근 확인 - 구글 로그인 페이지로 리다이렉트
    @Test
    public void testOAuth2LoginEndpoint() throws Exception {
        log.info("Testing GET /oauth2/authorization/google");

        mockMvc.perform(get("/oauth2/authorization/google"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        log.info("OAuth2 login endpoint test passed");
    }

    // 비로그인 상태에서 마이페이지 접근 - 리다이렉트
    @Test
    public void testOAuth2_unauthorized() throws Exception {
        log.info("Testing GET /api/mypage - unauthorized");

        mockMvc.perform(get("/api/mypage"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        log.info("OAuth2 unauthorized test passed");
    }

    // OAuth2 유저로 마이페이지 접근 성공
    @Test
    public void testOAuth2Login_accessMyPage() throws Exception {
        log.info("Testing GET /api/mypage - OAuth2 user");

        // DB에서 저장된 유저로 직접 인증 설정
        org.springframework.security.core.userdetails.UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("oauth2_user");

        mockMvc.perform(get("/api/mypage")
                        .with(org.springframework.security.test.web.servlet.request
                                .SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userid").value("oauth2_user"));

        log.info("OAuth2 access mypage test passed");
    }
}