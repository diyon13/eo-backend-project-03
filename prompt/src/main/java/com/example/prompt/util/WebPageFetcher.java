package com.example.prompt.util;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 웹 URL에서 페이지 본문 텍스트를 추출하는 유틸리티
 * jsoup으로 HTML 파싱 후 본문 텍스트만 추출
 */
@Component
@Slf4j
public class WebPageFetcher {

    private static final int MAX_CONTENT_LENGTH = 50_000;

    /**
     * URL에서 페이지 본문 텍스트 추출
     */
    public String fetchPageText(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL을 입력해주세요.");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("올바른 URL 형식이 아닙니다. (https://... 형태로 입력해주세요)");
        }

        log.info("웹 페이지 텍스트 추출 시작 - url = {}", url);

        String html = fetchHtml(url);
        String text = extractText(html);

        if (text.isBlank()) {
            throw new IllegalArgumentException("페이지에서 텍스트를 추출할 수 없습니다. 다른 URL을 시도해주세요.");
        }

        if (text.length() > MAX_CONTENT_LENGTH) {
            text = text.substring(0, MAX_CONTENT_LENGTH);
            log.info("텍스트 길이 초과로 잘라냄 - {} 자로 제한", MAX_CONTENT_LENGTH);
        }

        log.info("웹 페이지 텍스트 추출 완료 - url = {}, length = {}", url, text.length());
        return text;
    }

    /**
     * WebClient로 HTML 조회
     */
    private String fetchHtml(String url) {
        try {
            WebClient client = WebClient.builder()
                    .defaultHeader("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0 Safari/537.36")
                    .defaultHeader("Accept", "text/html,application/xhtml+xml")
                    .defaultHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8")
                    .codecs(config -> config.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                    .build();

            String html = client.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (html == null || html.isBlank()) {
                throw new IllegalArgumentException("페이지를 불러오지 못했습니다.");
            }
            return html;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("웹 페이지 조회 실패 - url = {}, error = {}", url, e.getMessage());
            throw new IllegalArgumentException("페이지에 접근할 수 없습니다. URL을 확인하거나 잠시 후 다시 시도해주세요.");
        }
    }

    /**
     * jsoup으로 HTML에서 본문 텍스트 추출
     * nav, header, footer, script, style 제거 후 텍스트만 추출
     */
    private String extractText(String html) {
        try {
            Document doc = Jsoup.parse(html);

            // 불필요한 태그 제거
            doc.select("script, style, nav, header, footer, aside, iframe, noscript").remove();

            // 본문 영역 우선 추출 (article > main > body 순)
            String text;
            if (!doc.select("article").isEmpty()) {
                text = doc.select("article").text();
            } else if (!doc.select("main").isEmpty()) {
                text = doc.select("main").text();
            } else {
                text = doc.body() != null ? doc.body().text() : doc.text();
            }

            return text.trim();

        } catch (Exception e) {
            log.warn("jsoup 파싱 실패, 정규식 fallback - error = {}", e.getMessage());
            return html.replaceAll("(?is)<script[^>]*>.*?</script>", " ")
                    .replaceAll("(?is)<style[^>]*>.*?</style>", " ")
                    .replaceAll("<[^>]+>", " ")
                    .replaceAll("\\s{2,}", " ")
                    .trim();
        }
    }
}