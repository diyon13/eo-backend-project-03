package com.example.prompt.config;

import com.example.prompt.security.CustomOAuth2UserService;
import com.example.prompt.security.CustomUserDetailsService;
import com.example.prompt.security.jwt.JwtAuthenticationFilter;
import com.example.prompt.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfiguration {

    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService oAuth2UserService;
    private final JwtProvider jwtProvider;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * JWT 체인 - /api/chat/** 제외 (세션 체인에서 처리)
     * "http://localhost:8080/api/..." 요청 처리
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // /api/chat/** 는 Order(2) 세션 체인에서 처리
                .securityMatcher("/api/users/**", "/api/email/**", "/api/admin/**",
                        "/api/user/**", "/api/payment/**", "/api/alan/**", "/api/stats/**")
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users",
                                "/api/users/check-id",
                                "/api/email/**",
                                "/api/user/reset-password",
                                "/api/admin/auth/login",
                                "/api/stats"
                        ).permitAll()
                        .requestMatchers("/api/admin/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                        )
                );

        return http.build();
    }

    /**
     * 세션 체인 - /api/chat/** 포함 (Form 로그인, OAuth2)
     * "http://localhost:8080/login", "http://localhost:8080/chat" 등 처리
     */
    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index",
                                "/login", "/signup",
                                "/reset-password",
                                "/payment",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/h2-console/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()
                        // 채팅 페이지 + 채팅 API - 세션 로그인 필요
                        .requestMatchers("/chat", "/api/chat/**").authenticated()
                        .requestMatchers("/mypage/password",
                                "/payment/checkout", "/payment/verify").authenticated()
                        .anyRequest().authenticated()
                )
                // Form 로그인
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("userid")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                // OAuth2 구글 로그인
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(ui -> ui.userService(oAuth2UserService))
                        .defaultSuccessUrl("/", true)
                )
                // 로그아웃
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/api/**")
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }
}