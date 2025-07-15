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
import static org.mockito.ArgumentMatchers.eq;

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
            // Given
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

            // When + Then
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
            // Given
            Mockito.when(applicablePriceUseCase.getApplicablePrice(any(), any(), any()))
                    .thenReturn(Mono.just(Optional.empty()));

            // When + Then
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
    }
}
