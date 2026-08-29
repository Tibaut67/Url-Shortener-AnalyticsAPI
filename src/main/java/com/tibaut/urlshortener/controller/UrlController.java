package com.tibaut.urlshortener.controller;


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
    public ResponseEntity<ShortenResponse> createShortUrl(@Valid @RequestBody ShortenRequest request, HttpServletRequest servletRequest) {
        String baseUrl = servletRequest.getRequestURL().toString()
                .replace(servletRequest.getRequestURI(), "");
        ShortenResponse response = urlService.shortenUrl(request, baseUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> getShortUrl(@PathVariable String shortCode) {

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlService.getOriginalUrl(shortCode)))
                .build();
    }
}
