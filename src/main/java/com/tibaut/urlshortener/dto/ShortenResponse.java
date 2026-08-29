package com.tibaut.urlshortener.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortenResponse {
    private String originalUrl;
    private String shortUrl;
    private String shortCode;
}
