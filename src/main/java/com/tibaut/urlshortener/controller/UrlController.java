package com.tibaut.urlshortener.controller;


import com.tibaut.urlshortener.dto.AnalyticsResponse;
import com.tibaut.urlshortener.dto.ShortenRequest;
import com.tibaut.urlshortener.dto.ShortenResponse;
import com.tibaut.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/api/v1/urls")
    public ResponseEntity<ShortenResponse> createShortUrl(
            @Valid @RequestBody ShortenRequest request,
            HttpServletRequest servletRequest) {
        String baseUrl = servletRequest.getRequestURL().toString()
                .replace(servletRequest.getRequestURI(), "");
        return ResponseEntity.status(HttpStatus.CREATED).body(urlService.shortenUrl(request, baseUrl));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToUrl(
            @PathVariable String shortCode,
            HttpServletRequest servletRequest) {
        String targetUrl = urlService.getOriginalUrlAndTrackClick(shortCode, servletRequest);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(targetUrl))
                .build();
    }

    @GetMapping("/api/v1/urls/{shortCode}/analytics")
    public ResponseEntity<AnalyticsResponse> getUrlAnalytics(@PathVariable String shortCode) {
        return ResponseEntity.ok(urlService.getAnalytics(shortCode));
    }
}
