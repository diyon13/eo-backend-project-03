package com.example.prompt.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanEntity {

    // 플랜 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    // 플랜 이름 : NORMAL, PRO , MAX
    @Column(name = "plan_name", nullable = false, length = 50)
    private String planName;

    //토큰 한도
    @Column(name = "token_limit", nullable = false)
    private int tokenLimit;

    // 사용량
    @Builder.Default
    @Column(name = "ai_use", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private int aiUse = 1;

    // 가격
    @Column(name = "price", nullable = false)
    private int price;

    // 생성일자
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
