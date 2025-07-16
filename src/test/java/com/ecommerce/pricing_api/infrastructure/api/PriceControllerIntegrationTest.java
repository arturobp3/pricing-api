package com.ecommerce.pricing_api.infrastructure.api;

import com.ecommerce.pricing_api.application.dto.ApplicablePriceResponseDto;
import com.ecommerce.pricing_api.application.usecases.ApplicablePriceUseCase;
import com.ecommerce.pricing_api.domain.model.ApplicablePrice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(controllers = PricesController.class)
class PricesControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ApplicablePriceUseCase applicablePriceUseCase;

    private static final String BASE_URL = "/api/v1/prices";

    @Nested
    @DisplayName("When fetching applicable price")
    class GetApplicablePriceTests {

        @Test
        @DisplayName("Should return 200 OK with price when found")
        void shouldReturnPriceWhenFound() {
            ApplicablePrice applicablePrice = new ApplicablePrice(
                    Optional.of(35455L),
                    Optional.of(1L),
                    Optional.of(2L),
                    Optional.of(LocalDateTime.now().minusDays(1)),
                    Optional.of(LocalDateTime.now().plusDays(1)),
                    Optional.of(BigDecimal.valueOf(25.50)),
                    Optional.of("EUR"),
                    Optional.of(1L)
            );

            Mockito.when(applicablePriceUseCase.getApplicablePrice(any(), any(), any()))
                    .thenReturn(Mono.just(Optional.of(applicablePrice)));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                            .queryParam("applicationDate", "2020-06-14T18:30:00")
                            .queryParam("productId", 35455)
                            .queryParam("brandId", 1)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(ApplicablePriceResponseDto.class)
                    .value(dto -> {
                        assert dto.productId().equals(35455L);
                        assert dto.brandId().equals(1L);
                    });
        }

        @Test
        @DisplayName("Should return 204 No Content when no applicable price found")
        void shouldReturnNoContentWhenNoPriceFound() {
            Mockito.when(applicablePriceUseCase.getApplicablePrice(any(), any(), any()))
                    .thenReturn(Mono.just(Optional.empty()));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                            .queryParam("applicationDate", "2020-06-14T18:30:00")
                            .queryParam("productId", 35455)
                            .queryParam("brandId", 1)
                            .build())
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("Should return 400 Bad Request when parameters are missing")
        void shouldReturnBadRequestWhenParamsMissing() {
            // When + Then
            webTestClient.get()
                    .uri(BASE_URL)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("Test 1: 14th June 10:00")
        void testAt10On14() {
            ApplicablePrice applicablePrice = new ApplicablePrice(
                    Optional.of(35455L), Optional.of(1L), Optional.of(1L),
                    Optional.of(LocalDateTime.of(2020, 6, 14, 0, 0)),
                    Optional.of(LocalDateTime.of(2020, 12, 31, 23, 59, 59)),
                    Optional.of(BigDecimal.valueOf(35.50)), Optional.of("EUR"), Optional.of(0L)
            );

            Mockito.when(applicablePriceUseCase.getApplicablePrice(any(), any(), any()))
                    .thenReturn(Mono.just(Optional.of(applicablePrice)));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                            .queryParam("applicationDate", "2020-06-14T10:00:00")
                            .queryParam("productId", 35455)
                            .queryParam("brandId", 1)
                            .build())
                    .exchange().expectStatus().isOk()
                    .expectBody(ApplicablePriceResponseDto.class)
                    .value(dto -> {
                        assert dto.productId().equals(35455L);
                        assert dto.brandId().equals(1L);
                        assert dto.priceList().equals(1L);
                        assert dto.startDate().isEqual(LocalDateTime.of(2020, 6, 14, 0, 0));
                        assert dto.endDate().isEqual(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
                        assert dto.price().compareTo(BigDecimal.valueOf(35.50)) == 0;
                        assert dto.currency().equals("EUR");
                    });
        }

        @Test
        @DisplayName("Test 2: 14th June 16:00")
        void testAt16On14() {
            ApplicablePrice applicablePrice = new ApplicablePrice(
                    Optional.of(35455L), Optional.of(1L), Optional.of(2L),
                    Optional.of(LocalDateTime.of(2020, 6, 14, 15, 0)),
                    Optional.of(LocalDateTime.of(2020, 6, 14, 18, 30)),
                    Optional.of(BigDecimal.valueOf(25.45)), Optional.of("EUR"), Optional.of(1L)
            );

            Mockito.when(applicablePriceUseCase.getApplicablePrice(any(), any(), any()))
                    .thenReturn(Mono.just(Optional.of(applicablePrice)));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                            .queryParam("applicationDate", "2020-06-14T16:00:00")
                            .queryParam("productId", 35455)
                            .queryParam("brandId", 1)
                            .build())
                    .exchange().expectStatus().isOk()
                    .expectBody(ApplicablePriceResponseDto.class)
                    .value(dto -> {
                        assert dto.productId().equals(35455L);
                        assert dto.brandId().equals(1L);
                        assert dto.priceList().equals(2L);
                        assert dto.startDate().isEqual(LocalDateTime.of(2020, 6, 14, 15, 0));
                        assert dto.endDate().isEqual(LocalDateTime.of(2020, 6, 14, 18, 30));
                        assert dto.price().compareTo(BigDecimal.valueOf(25.45)) == 0;
                        assert dto.currency().equals("EUR");
                    });
        }

        @Test
        @DisplayName("Test 3: 14th June 21:00")
        void testAt21On14() {
            ApplicablePrice applicablePrice = new ApplicablePrice(
                    Optional.of(35455L), Optional.of(1L), Optional.of(1L),
                    Optional.of(LocalDateTime.of(2020, 6, 14, 0, 0)),
                    Optional.of(LocalDateTime.of(2020, 12, 31, 23, 59, 59)),
                    Optional.of(BigDecimal.valueOf(35.50)), Optional.of("EUR"), Optional.of(0L)
            );

            Mockito.when(applicablePriceUseCase.getApplicablePrice(any(), any(), any()))
                    .thenReturn(Mono.just(Optional.of(applicablePrice)));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                            .queryParam("applicationDate", "2020-06-14T21:00:00")
                            .queryParam("productId", 35455)
                            .queryParam("brandId", 1)
                            .build())
                    .exchange().expectStatus().isOk()
                    .expectBody(ApplicablePriceResponseDto.class)
                    .value(dto -> {
                        assert dto.productId().equals(35455L);
                        assert dto.brandId().equals(1L);
                        assert dto.priceList().equals(1L);
                        assert dto.startDate().isEqual(LocalDateTime.of(2020, 6, 14, 0, 0));
                        assert dto.endDate().isEqual(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
                        assert dto.price().compareTo(BigDecimal.valueOf(35.50)) == 0;
                        assert dto.currency().equals("EUR");
                    });
        }

        @Test
        @DisplayName("Test 4: 15th June 10:00")
        void testAt10On15() {
            ApplicablePrice applicablePrice = new ApplicablePrice(
                    Optional.of(35455L), Optional.of(1L), Optional.of(3L),
                    Optional.of(LocalDateTime.of(2020, 6, 15, 0, 0)),
                    Optional.of(LocalDateTime.of(2020, 6, 15, 11, 0)),
                    Optional.of(BigDecimal.valueOf(30.50)), Optional.of("EUR"), Optional.of(1L)
            );

            Mockito.when(applicablePriceUseCase.getApplicablePrice(any(), any(), any()))
                    .thenReturn(Mono.just(Optional.of(applicablePrice)));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                            .queryParam("applicationDate", "2020-06-15T10:00:00")
                            .queryParam("productId", 35455)
                            .queryParam("brandId", 1)
                            .build())
                    .exchange().expectStatus().isOk()
                    .expectBody(ApplicablePriceResponseDto.class)
                    .value(dto -> {
                        assert dto.productId().equals(35455L);
                        assert dto.brandId().equals(1L);
                        assert dto.priceList().equals(3L);
                        assert dto.startDate().isEqual(LocalDateTime.of(2020, 6, 15, 0, 0));
                        assert dto.endDate().isEqual(LocalDateTime.of(2020, 6, 15, 11, 0));
                        assert dto.price().compareTo(BigDecimal.valueOf(30.50)) == 0;
                        assert dto.currency().equals("EUR");
                    });
        }

        @Test
        @DisplayName("Test 5: 16th June 21:00")
        void testAt21On16() {
            ApplicablePrice applicablePrice = new ApplicablePrice(
                    Optional.of(35455L), Optional.of(1L), Optional.of(4L),
                    Optional.of(LocalDateTime.of(2020, 6, 15, 16, 0)),
                    Optional.of(LocalDateTime.of(2020, 12, 31, 23, 59, 59)),
                    Optional.of(BigDecimal.valueOf(38.95)), Optional.of("EUR"), Optional.of(1L)
            );

            Mockito.when(applicablePriceUseCase.getApplicablePrice(any(), any(), any()))
                    .thenReturn(Mono.just(Optional.of(applicablePrice)));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                            .queryParam("applicationDate", "2020-06-16T21:00:00")
                            .queryParam("productId", 35455)
                            .queryParam("brandId", 1)
                            .build())
                    .exchange().expectStatus().isOk()
                    .expectBody(ApplicablePriceResponseDto.class)
                    .value(dto -> {
                        assert dto.productId().equals(35455L);
                        assert dto.brandId().equals(1L);
                        assert dto.priceList().equals(4L);
                        assert dto.startDate().isEqual(LocalDateTime.of(2020, 6, 15, 16, 0));
                        assert dto.endDate().isEqual(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
                        assert dto.price().compareTo(BigDecimal.valueOf(38.95)) == 0;
                        assert dto.currency().equals("EUR");
                    });
        }

    }
}
