package com.ecommerce.pricing_api.application.service;

import com.ecommerce.pricing_api.application.repository.PricesCacheRepository;
import com.ecommerce.pricing_api.application.repository.PricesDatabaseRepository;
import com.ecommerce.pricing_api.domain.model.ApplicablePrice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ApplicablePriceService.class)
class ApplicablePriceServiceIntegrationTest {

    @Autowired
    private ApplicablePriceService service;

    @MockBean
    private PricesCacheRepository pricesCacheRepository;

    @MockBean
    private PricesDatabaseRepository pricesDatabaseRepository;

    private ApplicablePrice buildSamplePrice(LocalDateTime now) {
        return new ApplicablePrice(
                Optional.of(35455L),
                Optional.of(1L),
                Optional.of(2L),
                Optional.of(now.minusHours(2)),
                Optional.of(now.plusHours(2)),
                Optional.of(BigDecimal.valueOf(25.99)),
                Optional.of("EUR"),
                Optional.of(1L)
        );
    }

    @Nested
    @DisplayName("Integration tests for ApplicablePriceService")
    class GetApplicablePrice {

        @Test
        @DisplayName("Should return price from cache if present")
        void shouldReturnFromCache() {
            LocalDateTime now = LocalDateTime.now();
            ApplicablePrice price = buildSamplePrice(now);

            when(pricesCacheRepository.find(any())).thenReturn(Flux.just(price));

            StepVerifier.create(service.getApplicablePrice(35455L, 1L, now))
                    .expectNextMatches(opt -> opt.isPresent() &&
                            opt.get().price().orElseThrow().compareTo(BigDecimal.valueOf(25.99)) == 0)
                    .verifyComplete();

            verify(pricesCacheRepository, times(2)).find(any());
            verifyNoInteractions(pricesDatabaseRepository);
        }

        @Test
        @DisplayName("Should return price from DB and cache it when cache is empty")
        void shouldFallbackToDatabase() {
            LocalDateTime now = LocalDateTime.now();
            ApplicablePrice dbPrice = buildSamplePrice(now);

            when(pricesCacheRepository.find(any())).thenReturn(Flux.empty());
            when(pricesDatabaseRepository.findAllByProductAndBrand(any(), any())).thenReturn(Flux.just(dbPrice));
            when(pricesCacheRepository.save(any(), any())).thenReturn(Mono.empty());

            StepVerifier.create(service.getApplicablePrice(35455L, 1L, now))
                    .expectNextMatches(opt -> opt.isPresent() &&
                            opt.get().productId().orElseThrow().equals(35455L))
                    .verifyComplete();

            verify(pricesCacheRepository, times(1)).find(any());
            verify(pricesDatabaseRepository, times(1)).findAllByProductAndBrand(35455L, 1L);
            verify(pricesCacheRepository, times(1)).save(any(), any());
        }

        @Test
        @DisplayName("Should return empty when no price matches")
        void shouldReturnEmptyWhenNoMatch() {
            LocalDateTime now = LocalDateTime.now();

            when(pricesCacheRepository.find(any())).thenReturn(Flux.empty());
            when(pricesDatabaseRepository.findAllByProductAndBrand(any(), any())).thenReturn(Flux.empty());

            StepVerifier.create(service.getApplicablePrice(35455L, 1L, now))
                    .expectNextMatches(Optional::isEmpty)
                    .verifyComplete();

            verify(pricesCacheRepository, times(1)).find(any());
            verify(pricesDatabaseRepository, times(1)).findAllByProductAndBrand(35455L, 1L);
        }
    }
}
