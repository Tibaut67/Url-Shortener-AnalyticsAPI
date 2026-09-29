package com.tibaut.urlshortener.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AnalyticsResponse {
    private String shortCode;
    private String originalUrl;
    private long totalClicks;
    private List<ClickEventDto> recentClicks;

    @Getter
    @Builder
    public static class ClickEventDto {
        private String referer;
        private String userAgent;
        private LocalDateTime clickedAt;
    }
}
