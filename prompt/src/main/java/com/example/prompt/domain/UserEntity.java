package com.example.prompt.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // plans 테이블 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id" , nullable = false)
    private PlanEntity plan;

    // admin 테이블 FK (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private AdminEntity admin;

    // 유저 아이디
    @Column(nullable = false, unique = true, length = 50)
    private String userid;

    // 유저 이름
    @Column(nullable = false, length = 50)
    private String username;

    // 비밀번호
    @Column(nullable = false, length = 255)
    private String password;

    // 이메일
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    // 사용 토큰량 (ERD: used_token)
    @Column(name = "used_token", nullable = false)
    @Builder.Default
    private int usedToken = 0;

    // 토큰 초기화 일자 (ERD: token_reset_at)
    @Column(name = "token_reset_at")
    private LocalDateTime tokenResetAt;

    // 플랜 만료일 (결제일 + 30일, NORMAL 플랜은 null)
    @Column(name = "plan_expired_at")
    private LocalDateTime planExpiredAt;

    // 활성여부 - 탈퇴시 false
    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private boolean active = true;

    // 계정 잠금 - 관리자가 잠금 처리
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean locked = false;

    // 구글 소셜 로그인
    @Column(name = "provider")
    private String provider;

    // 구글 소셜 로그인 아이디
    @Column(name = "provider_id")
    private String providerId;

    // 생성일자
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 수정일자
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}