package com.example.prompt.client;

import com.example.prompt.dto.alan.AlanAiDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class AlanAiClientTest {

    @Autowired
    private AlanAiClient alanAiClient;

    /**
     * 페이지 요약 테스트
     * [TEST] AlanAiClientTest#testSummarizePage
     */
    @Test
    void testSummarizePage() {
        String content = "[TEST] AlanAiClientTest#testSummarizePage - " +
                "Spring Boot는 자바 기반의 오픈소스 웹 프레임워크입니다. " +
                "빠른 개발과 배포를 지원하며, 내장 서버를 포함하고 있습니다.";

        log.info("페이지 요약 요청 전 - content = {}", content);
        String result = alanAiClient.summarizePage(content);
        log.info("페이지 요약 결과 = {}", result);

        assertThat(result).isNotNull();
        assertThat(result).isNotBlank();
    }

    /**
     * 페이지 번역 테스트
     * [TEST] AlanAiClientTest#testTranslatePage
     */
    @Test
    void testTranslatePage() {
        AlanAiDto.PageTranslateRequest request = new AlanAiDto.PageTranslateRequest(
                List.of("Hello, how are you?", "Spring Boot is a great framework.")
        );

        log.info("페이지 번역 요청 전 - contents = {}", request.getContents());
        String result = alanAiClient.translatePage(request);
        log.info("페이지 번역 결과 = {}", result);

        assertThat(result).isNotNull();
        assertThat(result).isNotBlank();
    }

    /**
     * 유튜브 자막 요약 테스트
     * [TEST] AlanAiClientTest#testSummarizeYoutube
     */
    @Test
    void testSummarizeYoutube() {
        AlanAiDto.YoutubeSubtitleRequest.SubtitleText t1 =
                new AlanAiDto.YoutubeSubtitleRequest.SubtitleText("0:00", "안녕하세요, 스프링 부트에 대해 알아보겠습니다.");
        AlanAiDto.YoutubeSubtitleRequest.SubtitleText t2 =
                new AlanAiDto.YoutubeSubtitleRequest.SubtitleText("0:10", "스프링 부트는 자바 웹 개발 프레임워크입니다.");

        AlanAiDto.YoutubeSubtitleRequest.Chapter chapter =
                new AlanAiDto.YoutubeSubtitleRequest.Chapter(0, "스프링 부트 소개", List.of(t1, t2));

        AlanAiDto.YoutubeSubtitleRequest request =
                new AlanAiDto.YoutubeSubtitleRequest(List.of(chapter));

        log.info("유튜브 자막 요약 요청 전 - chapters size = {}", request.getSubtitle().size());
        AlanAiDto.YoutubeSubtitleResponse result = alanAiClient.summarizeYoutube(request);
        log.info("유튜브 자막 요약 결과 = {}", result);

        assertThat(result).isNotNull();
        assertThat(result.getSummary()).isNotNull();
    }
}