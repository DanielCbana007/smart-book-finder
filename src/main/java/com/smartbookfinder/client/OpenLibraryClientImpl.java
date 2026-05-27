package com.smartbookfinder.client;

import com.smartbookfinder.entity.SupportedLanguage;
import com.smartbookfinder.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class OpenLibraryClientImpl implements OpenLibraryClient {

    private static final Map<SupportedLanguage, String> LANGUAGE_MAP = Map.of(
            SupportedLanguage.EN, "eng",
            SupportedLanguage.ES, "spa",
            SupportedLanguage.FR, "fre",
            SupportedLanguage.DE, "ger",
            SupportedLanguage.PT, "por"
    );

    private final RestClient restClient;
    private final String baseUrl;

    public OpenLibraryClientImpl(
            RestClient.Builder restClientBuilder,
            @Value("${openlibrary.base-url}") String baseUrl
    ) {
        this.restClient = restClientBuilder.build();
        this.baseUrl = baseUrl;
    }

    @Override
    public OpenLibrarySearchResponse search(String title, String author, SupportedLanguage language, int limit) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/search.json")
                    .queryParamIfPresent("title", java.util.Optional.ofNullable(title).filter(s -> !s.isBlank()))
                    .queryParamIfPresent("author", java.util.Optional.ofNullable(author).filter(s -> !s.isBlank()))
                    .queryParamIfPresent("language", java.util.Optional.ofNullable(language).map(LANGUAGE_MAP::get))
                    .queryParam("limit", Math.max(1, limit))
                    .build()
                    .toUriString();

            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(OpenLibrarySearchResponse.class);
        } catch (Exception e) {
            throw new ExternalApiException("Failed to fetch books from Open Library", e);
        }
    }
}
