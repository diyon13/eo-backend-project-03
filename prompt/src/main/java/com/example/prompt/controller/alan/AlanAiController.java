package com.example.prompt.controller.alan;

import com.example.prompt.dto.alan.AlanAiDto;
import com.example.prompt.dto.common.ApiResponse;
import com.example.prompt.service.AlanAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alan")
@RequiredArgsConstructor
public class AlanAiController {

    private final AlanAiService alanAiService;

    /**
     * 페이지 요약
     * POST "http://localhost:8080/api/alan/page/summary"
     * Body: { "content": "요약할 페이지 내용" }
     */
    @PostMapping("/page/summary")
    public ApiResponse<AlanAiDto.PageSummaryResponse> summarizePage(
            @RequestBody AlanAiDto.PageSummaryRequest request
    ) {
        return ApiResponse.ok(alanAiService.summarizePage(request));
    }

    /**
     * 페이지 번역
     * POST "http://localhost:8080/api/alan/page/translate"
     * Body: { "contents": ["번역할 텍스트1", "번역할 텍스트2"] }
     */
    @PostMapping("/page/translate")
    public ApiResponse<AlanAiDto.PageTranslateResponse> translatePage(
            @RequestBody AlanAiDto.PageTranslateRequest request
    ) {
        return ApiResponse.ok(alanAiService.translatePage(request));
    }

    /**
     * 유튜브 자막 요약
     * POST "http://localhost:8080/api/alan/youtube/summary"
     * Body: { "subtitle": [{ "chapterIdx": 0, "chapterTitle": "제목", "text": [{ "timestamp": "0:00", "content": "내용" }] }] }
     */
    @PostMapping("/youtube/summary")
    public ApiResponse<AlanAiDto.YoutubeSubtitleResponse> summarizeYoutube(
            @RequestBody AlanAiDto.YoutubeSubtitleRequest request
    ) {
        return ApiResponse.ok(alanAiService.summarizeYoutube(request));
    }
}