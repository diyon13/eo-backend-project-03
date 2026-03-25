package com.example.prompt.service;

import com.example.prompt.client.AlanAiClient;
import com.example.prompt.domain.UserEntity;
import com.example.prompt.dto.alan.AlanAiDto;
import com.example.prompt.repository.UserRepository;
import com.example.prompt.util.WebPageFetcher;
import com.example.prompt.util.YouTubeSubtitleFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlanAiService {

    private final AlanAiClient alanAiClient;
    private final UserRepository userRepository;
    private final WebPageFetcher webPageFetcher;
    private final YouTubeSubtitleFetcher youTubeSubtitleFetcher;

    /**
     * 페이지 요약 - 텍스트 직접 입력
     */
    @Transactional
    public AlanAiDto.PageSummaryResponse summarizePage(AlanAiDto.PageSummaryRequest request, Long userId) {
        log.info("페이지 요약 서비스 호출 - userId = {}", userId);

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("요약할 내용을 입력해주세요.");
        }

        UserEntity user = checkAndGetUser(userId);

        String result = alanAiClient.summarizePage(request.getContent());

        int tokensUsed = (request.getContent().length() + (result != null ? result.length() : 0)) / 4;
        deductToken(user, tokensUsed);
        log.info("페이지 요약 토큰 차감 - userId = {}, tokensUsed = {}", userId, tokensUsed);

        return AlanAiDto.PageSummaryResponse.builder()
                .summary(result)
                .build();
    }

    /**
     * 페이지 요약 - URL 입력
     * URL → 본문 텍스트 추출 → Alan AI 요약
     */
    @Transactional
    public AlanAiDto.PageSummaryResponse summarizePageByUrl(AlanAiDto.PageSummaryByUrlRequest request, Long userId) {
        log.info("페이지 URL 요약 서비스 호출 - userId = {}, url = {}", userId, request.getUrl());

        if (request.getUrl() == null || request.getUrl().isBlank()) {
            throw new IllegalArgumentException("URL을 입력해주세요.");
        }

        UserEntity user = checkAndGetUser(userId);

        String content = webPageFetcher.fetchPageText(request.getUrl());
        String result = alanAiClient.summarizePage(content);

        int tokensUsed = (content.length() + (result != null ? result.length() : 0)) / 4;
        deductToken(user, tokensUsed);
        log.info("페이지 URL 요약 토큰 차감 - userId = {}, tokensUsed = {}", userId, tokensUsed);

        return AlanAiDto.PageSummaryResponse.builder()
                .summary(result)
                .build();
    }

    /**
     * 페이지 번역 - 텍스트 직접 입력
     */
    @Transactional
    public AlanAiDto.PageTranslateResponse translatePage(AlanAiDto.PageTranslateRequest request, Long userId) {
        log.info("페이지 번역 서비스 호출 - userId = {}, contents size = {}", userId, request.getContents().size());

        if (request.getContents() == null || request.getContents().isEmpty()) {
            throw new IllegalArgumentException("번역할 내용을 입력해주세요.");
        }

        UserEntity user = checkAndGetUser(userId);

        String result = alanAiClient.translatePage(request);

        int inputLength = request.getContents().stream().mapToInt(String::length).sum();
        int tokensUsed = (inputLength + (result != null ? result.length() : 0)) / 4;
        deductToken(user, tokensUsed);
        log.info("페이지 번역 토큰 차감 - userId = {}, tokensUsed = {}", userId, tokensUsed);

        return AlanAiDto.PageTranslateResponse.builder()
                .translated(result)
                .build();
    }

    /**
     * 페이지 번역 - URL 입력
     * URL → 본문 텍스트 추출 → 단락 분리 → Alan AI 번역
     */
    @Transactional
    public AlanAiDto.PageTranslateResponse translatePageByUrl(AlanAiDto.PageTranslateByUrlRequest request, Long userId) {
        log.info("페이지 URL 번역 서비스 호출 - userId = {}, url = {}", userId, request.getUrl());

        if (request.getUrl() == null || request.getUrl().isBlank()) {
            throw new IllegalArgumentException("URL을 입력해주세요.");
        }

        UserEntity user = checkAndGetUser(userId);

        // URL에서 텍스트 추출 후 단락 단위로 분리
        String content = webPageFetcher.fetchPageText(request.getUrl());
        List<String> contents = Arrays.stream(content.split("\n\n"))
                .filter(s -> !s.isBlank())
                .toList();

        AlanAiDto.PageTranslateRequest translateRequest = new AlanAiDto.PageTranslateRequest(contents);
        String result = alanAiClient.translatePage(translateRequest);

        int tokensUsed = (content.length() + (result != null ? result.length() : 0)) / 4;
        deductToken(user, tokensUsed);
        log.info("페이지 URL 번역 토큰 차감 - userId = {}, tokensUsed = {}", userId, tokensUsed);

        return AlanAiDto.PageTranslateResponse.builder()
                .translated(result)
                .build();
    }

    /**
     * 유튜브 자막 요약 - 직접 입력
     */
    @Transactional
    public AlanAiDto.YoutubeSubtitleResponse summarizeYoutube(AlanAiDto.YoutubeSubtitleRequest request, Long userId) {
        log.info("유튜브 자막 요약 서비스 호출 - userId = {}, chapters size = {}", userId, request.getSubtitle().size());

        if (request.getSubtitle() == null || request.getSubtitle().isEmpty()) {
            throw new IllegalArgumentException("자막 데이터를 입력해주세요.");
        }

        UserEntity user = checkAndGetUser(userId);

        AlanAiDto.YoutubeSubtitleResponse result = alanAiClient.summarizeYoutube(request);

        int inputLength = request.getSubtitle().stream()
                .flatMap(chapter -> chapter.getText().stream())
                .mapToInt(text -> text.getContent().length())
                .sum();
        int tokensUsed = inputLength / 4;
        deductToken(user, tokensUsed);
        log.info("유튜브 요약 토큰 차감 - userId = {}, tokensUsed = {}", userId, tokensUsed);

        return result;
    }

    /**
     * 유튜브 URL 자막 자동 추출 후 요약
     */
    @Transactional
    public AlanAiDto.YoutubeSubtitleResponse summarizeYoutubeByUrl(AlanAiDto.YoutubeUrlRequest request, Long userId) {
        log.info("유튜브 URL 요약 서비스 호출 - userId = {}, url = {}", userId, request.getUrl());

        if (request.getUrl() == null || request.getUrl().isBlank()) {
            throw new IllegalArgumentException("YouTube URL을 입력해주세요.");
        }

        UserEntity user = checkAndGetUser(userId);

        // YouTube URL에서 자막 자동 추출
        AlanAiDto.YoutubeSubtitleRequest subtitleRequest = youTubeSubtitleFetcher.fetchSubtitles(request.getUrl());
        log.info("자막 추출 완료 - userId = {}, 챕터 수 = {}", userId, subtitleRequest.getSubtitle().size());

        AlanAiDto.YoutubeSubtitleResponse result = alanAiClient.summarizeYoutube(subtitleRequest);

        int inputLength = subtitleRequest.getSubtitle().stream()
                .flatMap(chapter -> chapter.getText().stream())
                .mapToInt(text -> text.getContent().length())
                .sum();
        int tokensUsed = inputLength / 4;
        deductToken(user, tokensUsed);
        log.info("유튜브 URL 요약 토큰 차감 - userId = {}, tokensUsed = {}", userId, tokensUsed);

        return result;
    }

    /**
     * 일반 질문 (단순 응답)
     */
    @Transactional
    public AlanAiDto.QuestionResponse question(AlanAiDto.QuestionRequest request, Long userId) {
        log.info("일반 질문 서비스 호출 - userId = {}", userId);

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("질문 내용을 입력해주세요.");
        }

        UserEntity user = checkAndGetUser(userId);

        String raw = alanAiClient.question(request.getContent());
        String answer = extractAnswerContent(raw);

        int tokensUsed = (request.getContent().length() + (answer != null ? answer.length() : 0)) / 4;
        deductToken(user, tokensUsed);
        log.info("일반 질문 토큰 차감 - userId = {}, tokensUsed = {}", userId, tokensUsed);

        return AlanAiDto.QuestionResponse.builder()
                .answer(answer)
                .build();
    }

    /**
     * plain-streaming 질문
     */
    @Transactional
    public reactor.core.publisher.Flux<String> plainStreaming(AlanAiDto.QuestionRequest request, Long userId) {
        log.info("plain-streaming 서비스 호출 - userId = {}", userId);

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("질문 내용을 입력해주세요.");
        }

        UserEntity user = checkAndGetUser(userId);
        int estimatedTokens = request.getContent().length() / 4;
        deductToken(user, estimatedTokens);

        return alanAiClient.plainStreaming(request.getContent());
    }

    /**
     * Alan AI question 응답에서 content 필드 추출
     */
    private String extractAnswerContent(String raw) {
        if (raw == null || raw.isBlank()) return "";
        try {
            int idx = raw.indexOf("\"content\":");
            if (idx == -1) return raw;
            int start = raw.indexOf("\"", idx + 10) + 1;
            if (start <= 0) return raw;
            int end = start;
            while (end < raw.length()) {
                char c = raw.charAt(end);
                if (c == '\\') { end += 2; continue; }
                if (c == '"') break;
                end++;
            }
            return raw.substring(start, end)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        } catch (Exception e) {
            log.warn("question 응답 파싱 실패 - raw = {}", raw);
            return raw;
        }
    }

    private UserEntity checkAndGetUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        int tokenLimit = user.getPlan().getTokenLimit();
        int usedToken  = user.getUsedToken();

        if (usedToken >= tokenLimit) {
            log.warn("토큰 한도 초과 - userId = {}, usedToken = {}, tokenLimit = {}", userId, usedToken, tokenLimit);
            throw new IllegalArgumentException("토큰 한도를 초과했습니다. 플랜을 업그레이드 해주세요.");
        }

        return user;
    }

    private void deductToken(UserEntity user, int tokensUsed) {
        int tokenLimit = user.getPlan().getTokenLimit();
        int newUsed    = Math.min(user.getUsedToken() + tokensUsed, tokenLimit);
        user.setUsedToken(newUsed);
        userRepository.save(user);
    }
}