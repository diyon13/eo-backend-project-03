package com.example.prompt.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 관리자 통계 조회 Dto
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDto {

    // 조회 기간 타입
    private String periodType;

    // 조회 시작 날짜
    private String startDate;

    // 조회 종료 날짜
    private String endDate;

    // 조회 대상 플랜
    private String planType;

    // 전체 채팅방 수
    private long totalChatRooms;

    // 전체 채팅 사용 수
    private long totalChats;

    // 전체 이미지 업로드 수
    private long totalImages;

    // 전체 파일 업로드 수
    private long totalFiles;

    // 전체 사용된 토큰 수
    private long totalUsedTokens;

    // 플랜별 통계 목록
    private List<PlanStat> planStats;

    // 기간별 통계 (페이징 처리)
    private Page<PeriodStat> statsPage;


    /**
     * 플랜별 통계 정보를 담는 내부 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanStat {
        // 플랜 이름
        private String planName;

        // 해당 플랜 사용자 수
        private long userCount;

        // 해당 플랜 사용자의 채팅방 수
        private long chatRoomCount;

        // 해당 플랜 사용자의 채팅 사용 수
        private long chatCount;

        // 해당 플랜 사용자의 이미지 업로드 수
        private long imageCount;

        // 해당 플랜 사용자의 파일 업로드 수
        private long fileCount;

        // 해당 플랜 사용자의 총 토큰 사용량
        private long usedTokens;
    }

    /**
     * 기간별 통계 정보를 담는 내부 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodStat {
        // 통계 날짜
        private String statDate;

        // 해당 날짜 가입자 수
        private long signupCount;

        // 해당 날짜 채팅방 생성 수
        private long chatRoomCount;

        // 해당 날짜 채팅 사용 수
        private long chatCount;

        // 해당 날짜 이미지 업로드 수
        private long imageCount;

        // 해당 날짜 파일 업로드 수
        private long fileCount;

        // 해당 날짜 토큰 사용량
        private long usedTokens;
    }
}