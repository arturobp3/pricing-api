package com.ecommerce.pricing_api.application.service;

import com.ecommerce.pricing_api.application.repository.PricesCacheRepository;
import com.ecommerce.pricing_api.application.repository.PricesDatabaseRepository;
import com.ecommerce.pricing_api.domain.model.ApplicablePrice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ApplicablePriceServiceTest {

    @Mock
    private PricesCacheRepository pricesCacheRepository;

    @Mock
    private PricesDatabaseRepository pricesDatabaseRepository;

    @InjectMocks
    private ApplicablePriceService applicablePriceService;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private ApplicablePrice buildPrice(Long priority, LocalDateTime start, LocalDateTime end) {
        return new ApplicablePrice(
                Optional.of(35455L),
                Optional.of(1L),
                Optional.of(2L),
                Optional.of(start),
                Optional.of(end),
                Optional.of(BigDecimal.valueOf(50.00)),
                Optional.of("EUR"),
                Optional.of(priority)
        );
    }

    @Nested
    @DisplayName("When fetching applicable prices")
    class FetchingPrices {

        @Test
        @DisplayName("Should return price from cache when available")
        void shouldReturnPriceFromCache() {
            ApplicablePrice price = buildPrice(1L, now.minusHours(1), now.plusHours(1));

            when(pricesCacheRepository.find(anyString())).thenReturn(Flux.just(price));

            Optional<ApplicablePrice> result = applicablePriceService.getApplicablePrice(35455L, 1L, now).block();

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(price);

            verify(pricesCacheRepository, times(2)).find(anyString());
            verifyNoInteractions(pricesDatabaseRepository);
        }

        @Test
        @DisplayName("Should fallback to database and save to cache if cache is empty")
        void shouldFallbackToDatabaseWhenCacheEmpty() {
            ApplicablePrice price = buildPrice(1L, now.minusHours(2), now.plusHours(2));

            when(pricesCacheRepository.find(anyString())).thenReturn(Flux.empty());
            when(pricesDatabaseRepository.findAllByProductAndBrand(anyLong(), anyLong())).thenReturn(Flux.just(price));
            when(pricesCacheRepository.save(anyString(), anyList())).thenReturn(Mono.empty());

            Optional<ApplicablePrice> result = applicablePriceService.getApplicablePrice(35455L, 1L, now).block();

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(price);
        }

        @Test
        @DisplayName("Should return empty when no prices available")
        void shouldReturnEmptyWhenNoPrices() {
            when(pricesCacheRepository.find(anyString())).thenReturn(Flux.empty());
            when(pricesDatabaseRepository.findAllByProductAndBrand(anyLong(), anyLong())).thenReturn(Flux.empty());

            Optional<ApplicablePrice> result = applicablePriceService.getApplicablePrice(35455L, 1L, now).block();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should select price with highest priority if multiple applicable")
        void shouldSelectHighestPriorityPrice() {
            ApplicablePrice lowPriority = buildPrice(1L, now.minusDays(1), now.plusDays(1));
            ApplicablePrice highPriority = buildPrice(10L, now.minusDays(1), now.plusDays(1));

            when(pricesCacheRepository.find(anyString())).thenReturn(Flux.just(lowPriority, highPriority));

            Optional<ApplicablePrice> result = applicablePriceService.getApplicablePrice(35455L, 1L, now).block();

            assertThat(result).isPresent();
            assertThat(result.get().priority()).contains(10L);
        }

        @Test
        @DisplayName("Should filter out prices not valid for applicationDate")
        void shouldFilterInvalidDates() {
            ApplicablePrice futurePrice = buildPrice(5L, now.plusDays(1), now.plusDays(2));

            when(pricesCacheRepository.find(anyString())).thenReturn(Flux.just(futurePrice));

            Optional<ApplicablePrice> result = applicablePriceService.getApplicablePrice(35455L, 1L, now).block();

            assertThat(result).isEmpty();
        }
    }
}
