package com.tibaut.urlshortener.service;


import com.tibaut.urlshortener.dto.AnalyticsResponse;
import com.tibaut.urlshortener.dto.ShortenRequest;
import com.tibaut.urlshortener.dto.ShortenResponse;
import com.tibaut.urlshortener.exception.ResourceNotFoundException;
import com.tibaut.urlshortener.model.ClickAnalytics;
import com.tibaut.urlshortener.model.Url;
import com.tibaut.urlshortener.repository.ClickAnalyticsRepository;
import com.tibaut.urlshortener.repository.UrlRepository;
import com.tibaut.urlshortener.util.Base62;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional
public class UrlService {
    private final UrlRepository urlRepository;
    private final ClickAnalyticsRepository clickAnalyticsRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public ShortenResponse shortenUrl(ShortenRequest request, String baseUrl) {
        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .build();
        Url savedUrl = urlRepository.save(url);

        String shortCode = Base62.encode(savedUrl.getId());
        savedUrl.setShortCode(shortCode);
        urlRepository.save(savedUrl);
        redisTemplate.opsForValue().set("url:" + shortCode, savedUrl.getOriginalUrl(), Duration.ofDays(1));

        return ShortenResponse.builder()
                .originalUrl(savedUrl.getOriginalUrl())
                .shortCode(shortCode)
                .shortUrl(baseUrl + "/" + shortCode)
                .build();

    }

    @Transactional
    public String getOriginalUrlAndTrackClick(String shortCode, HttpServletRequest request) {
        // 1. Resolve URL from cache or database
        String cacheKey = "url:" + shortCode;
        String originalUrl = redisTemplate.opsForValue().get(cacheKey);

        Url url = null;
        if (originalUrl == null) {
            url = urlRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new ResourceNotFoundException("URL not found for short code: " + shortCode));
            originalUrl = url.getOriginalUrl();
            redisTemplate.opsForValue().set(cacheKey, originalUrl, Duration.ofDays(1));
        }

        // 2. Increment Redis click counter
        redisTemplate.opsForValue().increment("clicks:" + shortCode);

        // 3. Persist analytical click event
        if (url == null) {
            url = urlRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new ResourceNotFoundException("URL not found for short code: " + shortCode));
        }

        ClickAnalytics click = ClickAnalytics.builder()
                .url(url)
                .referer(request.getHeader("Referer"))
                .userAgent(request.getHeader("User-Agent"))
                .build();
        clickAnalyticsRepository.save(click);

        return originalUrl;
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String shortCode) {
        String cacheKey = "url:" + shortCode;
        String cachedUrl = redisTemplate.opsForValue().get(cacheKey);
        if (cachedUrl != null) {
            return cachedUrl;
        }
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found for short code: " + shortCode));

        redisTemplate.opsForValue().set(cacheKey, url.getOriginalUrl(), Duration.ofDays(1));

        return url.getOriginalUrl();
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalytics(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("URL not found for short code: " + shortCode));

        // Optional: read total clicks from Redis counter, falling back to DB count
        String clickCountStr = redisTemplate.opsForValue().get("clicks:" + shortCode);
        long totalClicks = (clickCountStr != null)
                ? Long.parseLong(clickCountStr)
                : clickAnalyticsRepository.countByUrl(url);

        return AnalyticsResponse.builder()
                .shortCode(shortCode)
                .originalUrl(url.getOriginalUrl())
                .totalClicks(totalClicks)
                .build();
    }
}
