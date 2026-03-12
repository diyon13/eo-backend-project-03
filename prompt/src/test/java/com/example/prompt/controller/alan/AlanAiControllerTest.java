package com.example.prompt.controller.alan;

import com.example.prompt.dto.alan.AlanAiDto;
import com.example.prompt.service.AlanAiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Slf4j
class AlanAiControllerTest {

    @Mock
    private AlanAiService alanAiService;

    @InjectMocks
    private AlanAiController alanAiController;

    /**
     * 페이지 요약 컨트롤러 단위 테스트
     * [TEST] AlanAiControllerTest#testSummarizePage
     */
    @Test
    void testSummarizePage() {
        AlanAiDto.PageSummaryRequest request = new AlanAiDto.PageSummaryRequest(
                "[TEST] AlanAiControllerTest#testSummarizePage - 테스트 내용"
        );
        AlanAiDto.PageSummaryResponse mockResponse = AlanAiDto.PageSummaryResponse.builder()
                .summary("테스트 요약 결과")
                .build();

        given(alanAiService.summarizePage(any())).willReturn(mockResponse);

        log.info("페이지 요약 컨트롤러 테스트 시작");

        var result = alanAiController.summarizePage(request);

        log.info("페이지 요약 컨트롤러 결과 = {}", result.getData().getSummary());
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData().getSummary()).isEqualTo("테스트 요약 결과");
    }

    /**
     * 페이지 번역 컨트롤러 단위 테스트
     * [TEST] AlanAiControllerTest#testTranslatePage
     */
    @Test
    void testTranslatePage() {
        AlanAiDto.PageTranslateRequest request = new AlanAiDto.PageTranslateRequest(
                List.of("Hello World")
        );
        AlanAiDto.PageTranslateResponse mockResponse = AlanAiDto.PageTranslateResponse.builder()
                .translated("안녕 세계")
                .build();

        given(alanAiService.translatePage(any())).willReturn(mockResponse);

        log.info("페이지 번역 컨트롤러 테스트 시작");

        var result = alanAiController.translatePage(request);

        log.info("페이지 번역 컨트롤러 결과 = {}", result.getData().getTranslated());
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData().getTranslated()).isEqualTo("안녕 세계");
    }

    /**
     * 유튜브 자막 요약 컨트롤러 단위 테스트
     * [TEST] AlanAiControllerTest#testSummarizeYoutube
     */
    @Test
    void testSummarizeYoutube() {
        AlanAiDto.YoutubeSubtitleRequest.SubtitleText text =
                new AlanAiDto.YoutubeSubtitleRequest.SubtitleText("0:00", "테스트 자막");
        AlanAiDto.YoutubeSubtitleRequest.Chapter chapter =
                new AlanAiDto.YoutubeSubtitleRequest.Chapter(0, "테스트 챕터", List.of(text));
        AlanAiDto.YoutubeSubtitleRequest request =
                new AlanAiDto.YoutubeSubtitleRequest(List.of(chapter));

        AlanAiDto.YoutubeSubtitleResponse.SummaryChapter summaryChapter =
                new AlanAiDto.YoutubeSubtitleResponse.SummaryChapter(
                        0, "테스트 챕터", "0:00", List.of("상세"), List.of("요약")
                );
        AlanAiDto.YoutubeSubtitleResponse.Summary summary =
                new AlanAiDto.YoutubeSubtitleResponse.Summary(List.of(summaryChapter), List.of("전체 요약"));
        AlanAiDto.YoutubeSubtitleResponse mockResponse =
                AlanAiDto.YoutubeSubtitleResponse.builder().summary(summary).build();

        given(alanAiService.summarizeYoutube(any())).willReturn(mockResponse);

        log.info("유튜브 자막 요약 컨트롤러 테스트 시작");

        var result = alanAiController.summarizeYoutube(request);

        log.info("유튜브 자막 요약 컨트롤러 결과 = {}", result.getData().getSummary().getChapters());
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData().getSummary().getChapters()).isNotEmpty();
        assertThat(result.getData().getSummary().getChapters().get(0).getTitle()).isEqualTo("테스트 챕터");
    }
}