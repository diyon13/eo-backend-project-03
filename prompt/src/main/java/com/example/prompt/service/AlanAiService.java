package com.example.prompt.service;

import com.example.prompt.client.AlanAiClient;
import com.example.prompt.dto.alan.AlanAiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlanAiService {

    private final AlanAiClient alanAiClient;

    /**
     * 페이지 요약
     */
    public AlanAiDto.PageSummaryResponse summarizePage(AlanAiDto.PageSummaryRequest request) {
        log.info("페이지 요약 서비스 호출");

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("요약할 내용을 입력해주세요.");
        }

        String result = alanAiClient.summarizePage(request.getContent());

        return AlanAiDto.PageSummaryResponse.builder()
                .summary(result)
                .build();
    }

    /**
     * 페이지 번역
     */
    public AlanAiDto.PageTranslateResponse translatePage(AlanAiDto.PageTranslateRequest request) {
        log.info("페이지 번역 서비스 호출 - contents size = {}", request.getContents().size());

        if (request.getContents() == null || request.getContents().isEmpty()) {
            throw new IllegalArgumentException("번역할 내용을 입력해주세요.");
        }

        String result = alanAiClient.translatePage(request);

        return AlanAiDto.PageTranslateResponse.builder()
                .translated(result)
                .build();
    }

    /**
     * 유튜브 자막 요약
     */
    public AlanAiDto.YoutubeSubtitleResponse summarizeYoutube(AlanAiDto.YoutubeSubtitleRequest request) {
        log.info("유튜브 자막 요약 서비스 호출 - chapters size = {}", request.getSubtitle().size());

        if (request.getSubtitle() == null || request.getSubtitle().isEmpty()) {
            throw new IllegalArgumentException("자막 데이터를 입력해주세요.");
        }

        return alanAiClient.summarizeYoutube(request);
    }
}