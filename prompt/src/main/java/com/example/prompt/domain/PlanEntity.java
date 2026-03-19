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

    // 일일 채팅 제한
    @Column(name = "daily_chat_limit", nullable = false)
    private int dailyChatLimit;

    // 이미지 업로드 제한
    @Column(name = "image_upload_limit", nullable = false)
    private int imageUploadLimit;

    // 파일 업로드 제한
    @Column(name = "file_upload_limit", nullable = false)
    private int fileUploadLimit;

    // 파일 최대 용량(MB)
    @Column(name = "file_size_limit", nullable = false)
    private int fileSizeLimit;

    // 생성일자
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 수정일자
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
