package com.tibaut.urlshortener.service;


import com.tibaut.urlshortener.dto.ShortenRequest;
import com.tibaut.urlshortener.dto.ShortenResponse;
import com.tibaut.urlshortener.model.Url;
import com.tibaut.urlshortener.repository.UrlRepository;
import com.tibaut.urlshortener.util.Base62;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UrlService {
    private final UrlRepository urlRepository;

    public ShortenResponse shortenUrl(ShortenRequest request, String baseUrl) {
        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .build();
        Url savedUrl = urlRepository.save(url);

        String shortCode = Base62.encode(savedUrl.getId());
        savedUrl.setShortCode(shortCode);
        urlRepository.save(savedUrl);

        return ShortenResponse.builder()
                .originalUrl(savedUrl.getOriginalUrl())
                .shortCode(shortCode)
                .shortUrl(baseUrl + "/" + shortCode)
                .build();
    }

    public String getOriginalUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found for short code: " + shortCode));

        return url.getOriginalUrl();
    }

}
