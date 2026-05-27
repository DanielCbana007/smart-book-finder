package com.smartbookfinder.integration;

import com.smartbookfinder.dto.request.FavoriteBookRequest;
import com.smartbookfinder.dto.response.BookSearchResponse;
import com.smartbookfinder.dto.response.FavoriteBookResponse;
import com.smartbookfinder.repository.FavoriteBookRepository;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 18080)
class BookSearchIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FavoriteBookRepository favoriteBookRepository;

    private final RestClient restClient = RestClient.create();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("openlibrary.base-url", () -> "http://localhost:18080");
    }

    @BeforeEach
    void setUp() {
        favoriteBookRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveFavorite() {
        FavoriteBookRequest request = new FavoriteBookRequest(
                "/works/OL123W", "Dune", "Frank Herbert",
                1965, 100, "https://covers.openlibrary.org/b/id/12345-M.jpg"
        );

        ResponseEntity<FavoriteBookResponse> createResponse = restClient.post()
                .uri("http://localhost:{port}/api/favorites", port)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(FavoriteBookResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().title()).isEqualTo("Dune");

        ResponseEntity<FavoriteBookResponse[]> listResponse = restClient.get()
                .uri("http://localhost:{port}/api/favorites", port)
                .retrieve()
                .toEntity(FavoriteBookResponse[].class);

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).hasSize(1);

        restClient.delete()
                .uri("http://localhost:{port}/api/favorites/{id}", port, createResponse.getBody().id())
                .retrieve()
                .toBodilessEntity();

        assertThat(favoriteBookRepository.count()).isZero();
    }

    @Test
    void searchHistory_shouldReturnPageableResults() {
        var response = restClient.get()
                .uri("http://localhost:{port}/api/history?page=0&size=10", port)
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"content\"");
    }

    @Test
    void searchEndpoint_shouldReturnResults() {
        String openLibraryResponse = """
                {
                    "numFound": 5,
                    "docs": [
                        {"title": "Dune", "author_name": ["Frank Herbert"], "first_publish_year": 1965, "edition_count": 100, "cover_i": 12345, "key": "/works/OL1W"},
                        {"title": "Dune Messiah", "author_name": ["Frank Herbert"], "first_publish_year": 1969, "edition_count": 50, "cover_i": 12346, "key": "/works/OL2W"},
                        {"title": "Children of Dune", "author_name": ["Frank Herbert"], "first_publish_year": 1976, "edition_count": 40, "cover_i": 12347, "key": "/works/OL3W"},
                        {"title": "God Emperor of Dune", "author_name": ["Frank Herbert"], "first_publish_year": 1981, "edition_count": 30, "cover_i": 12348, "key": "/works/OL4W"},
                        {"title": "Heretics of Dune", "author_name": ["Frank Herbert"], "first_publish_year": 1984, "edition_count": 25, "cover_i": 12349, "key": "/works/OL5W"}
                    ]
                }
                """;

        stubFor(WireMock.get(urlPathEqualTo("/search.json"))
                .withQueryParam("title", equalTo("Dune"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(openLibraryResponse)));

        ResponseEntity<BookSearchResponse> response = restClient.get()
                .uri("http://localhost:{port}/api/books/search?title=Dune", port)
                .retrieve()
                .toEntity(BookSearchResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().books()).hasSize(3);
        assertThat(response.getBody().books().get(0).title()).isEqualTo("Dune");
    }

    @Test
    void searchEndpoint_shouldReturn404_whenInsufficientResults() {
        String openLibraryResponse = """
                {"numFound": 0, "docs": []}
                """;

        stubFor(WireMock.get(urlPathEqualTo("/search.json"))
                .withQueryParam("title", equalTo("UnknownBook"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(openLibraryResponse)));

        ResponseEntity<String> response = restClient.get()
                .uri("http://localhost:{port}/api/books/search?title=UnknownBook", port)
                .retrieve()
                .onStatus(status -> status.value() >= 400, (req, res) -> {})
                .toEntity(String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }
}