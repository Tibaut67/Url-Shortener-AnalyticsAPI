package com.tibaut.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.URL;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShortenRequest {

    @NotBlank(message = "URL cannot be empty")
    @URL(message = "Invalid URL format")
    private String originalUrl;


}
