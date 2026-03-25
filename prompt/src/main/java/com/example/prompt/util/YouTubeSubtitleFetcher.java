package com.example.prompt.util;

import com.example.prompt.dto.alan.AlanAiDto;
import io.github.thoroldvix.api.TranscriptApiFactory;
import io.github.thoroldvix.api.TranscriptContent;
import io.github.thoroldvix.api.YoutubeTranscriptApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * YouTube URL에서 자막을 자동으로 추출하는 유틸리티
 */
@Component
@Slf4j
public class YouTubeSubtitleFetcher {

    /**
     * YouTube URL에서 자막 추출
     */
    public AlanAiDto.YoutubeSubtitleRequest fetchSubtitles(String url) {
        String videoId = extractVideoId(url);
        if (videoId == null) {
            throw new IllegalArgumentException("올바른 YouTube URL이 아닙니다. (예: https://www.youtube.com/watch?v=xxxxx)");
        }
        log.info("YouTube 자막 추출 시작 - videoId = {}", videoId);

        List<AlanAiDto.YoutubeSubtitleRequest.Chapter.SubtitleText> subtitles = fetchTranscript(videoId);

        if (subtitles.isEmpty()) {
            throw new IllegalArgumentException("이 영상에는 자막이 제공되지 않습니다. 자막이 있는 영상의 URL을 입력해주세요.");
        }

        log.info("YouTube 자막 추출 완료 - videoId = {}, 자막 수 = {}", videoId, subtitles.size());

        AlanAiDto.YoutubeSubtitleRequest.Chapter chapter =
                new AlanAiDto.YoutubeSubtitleRequest.Chapter(0, "동영상 내용", subtitles);

        return new AlanAiDto.YoutubeSubtitleRequest(List.of(chapter));
    }

    /**
     * thoroldvix 라이브러리로 자막 추출
     * 한국어 우선, 없으면 영어로 자동 fallback
     */
    private List<AlanAiDto.YoutubeSubtitleRequest.Chapter.SubtitleText> fetchTranscript(String videoId) {
        try {
            YoutubeTranscriptApi api = TranscriptApiFactory.createDefault();

            // "ko" 한국어 우선, 없으면 "en" 영어로 fallback
            TranscriptContent content = api.getTranscript(videoId, "ko", "en");

            List<AlanAiDto.YoutubeSubtitleRequest.Chapter.SubtitleText> result = new ArrayList<>();
            content.getContent().forEach(fragment -> {
                // 시작 시간을 "분:초" 포맷으로 변환
                String timestamp = LocalTime.ofSecondOfDay((long) fragment.getStart() % 86400)
                        .format(DateTimeFormatter.ofPattern("mm:ss"));
                String text = fragment.getText().replace("\n", " ").trim();

                if (!text.isEmpty()) {
                    result.add(new AlanAiDto.YoutubeSubtitleRequest.Chapter.SubtitleText(timestamp, text));
                }
            });

            log.info("자막 추출 성공 - videoId = {}, 항목 수 = {}", videoId, result.size());
            return result;

        } catch (Exception e) {
            log.error("자막 추출 실패 - videoId = {}, error = {}", videoId, e.getMessage());
            return List.of();
        }
    }

    /**
     * YouTube URL에서 videoId 추출
     */
    public String extractVideoId(String url) {
        if (url == null || url.isBlank()) return null;

        Matcher m;
        m = Pattern.compile("[?&]v=([a-zA-Z0-9_-]{11})").matcher(url);
        if (m.find()) return m.group(1);

        m = Pattern.compile("youtu\\.be/([a-zA-Z0-9_-]{11})").matcher(url);
        if (m.find()) return m.group(1);

        m = Pattern.compile("shorts/([a-zA-Z0-9_-]{11})").matcher(url);
        if (m.find()) return m.group(1);

        return null;
    }
}