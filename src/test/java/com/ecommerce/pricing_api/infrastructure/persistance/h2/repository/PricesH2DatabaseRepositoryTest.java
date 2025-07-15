package com.ecommerce.pricing_api.infrastructure.persistance.h2.repository;

import com.ecommerce.pricing_api.domain.model.ApplicablePrice;
import io.r2dbc.spi.Row;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PricesH2DatabaseRepositoryTest {

    private final DatabaseClient databaseClient = mock(DatabaseClient.class);
    private final DatabaseClient.GenericExecuteSpec executeSpec = mock(DatabaseClient.GenericExecuteSpec.class);
    private final DatabaseClient.GenericExecuteSpec bindSpec = mock(DatabaseClient.GenericExecuteSpec.class);
    private final RowsFetchSpec<ApplicablePrice> fetchSpec = mock(RowsFetchSpec.class);

    private final PricesH2DatabaseRepository repository = new PricesH2DatabaseRepository(databaseClient);

    @Test
    @DisplayName("Should find prices from database and map correctly")
    void shouldFindPricesFromDatabase() {
        // Given
        ApplicablePrice mockPrice = new ApplicablePrice(
                Optional.of(35455L),
                Optional.of(1L),
                Optional.of(2L),
                Optional.of(LocalDateTime.now().minusDays(1)),
                Optional.of(LocalDateTime.now().plusDays(1)),
                Optional.of(BigDecimal.valueOf(50.00)),
                Optional.of("EUR"),
                Optional.of(1L)
        );

        when(databaseClient.sql(anyString())).thenReturn(executeSpec);
        when(executeSpec.bind(eq("productId"), any())).thenReturn(bindSpec);
        when(bindSpec.bind(eq("brandId"), any())).thenReturn(bindSpec);
        when(bindSpec.map(any(BiFunction.class))).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn(Flux.just(mockPrice));

        // When
        Flux<ApplicablePrice> result = repository.findAllByProductAndBrand(35455L, 1L);

        // Then
        assertThat(result.collectList().block())
                .isNotNull()
                .hasSize(1)
                .first()
                .satisfies(price -> {
                    assertThat(price.productId()).contains(35455L);
                    assertThat(price.brandId()).contains(1L);
                    assertThat(price.priceList()).contains(2L);
                    assertThat(price.price()).contains(BigDecimal.valueOf(50.00));
                    assertThat(price.currency()).contains("EUR");
                    assertThat(price.priority()).contains(1L);
                });

        verify(databaseClient, times(1)).sql(anyString());
        verify(executeSpec, times(1)).bind(eq("productId"), any());
        verify(bindSpec, times(1)).bind(eq("brandId"), any());
        verify(bindSpec, times(1)).map(any(BiFunction.class));
        verify(fetchSpec, times(1)).all();
    }

    @Test
    @DisplayName("Should return empty when no prices found in database")
    void shouldReturnEmptyWhenNoPricesFound() {
        // Given
        when(databaseClient.sql(anyString())).thenReturn(executeSpec);
        when(executeSpec.bind(eq("productId"), any())).thenReturn(bindSpec);
        when(bindSpec.bind(eq("brandId"), any())).thenReturn(bindSpec);
        when(bindSpec.map(any(BiFunction.class))).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn(Flux.empty());

        // When
        Flux<ApplicablePrice> result = repository.findAllByProductAndBrand(99999L, 999L);

        // Then
        assertThat(result.collectList().block())
                .isNotNull()
                .isEmpty();

        verify(databaseClient, times(1)).sql(anyString());
        verify(executeSpec, times(1)).bind(eq("productId"), any());
        verify(bindSpec, times(1)).bind(eq("brandId"), any());
        verify(bindSpec, times(1)).map(any(BiFunction.class));
        verify(fetchSpec, times(1)).all();
    }

    @Test
    @DisplayName("Should map null fields from database to empty Optional")
    void shouldMapNullFieldsToEmptyOptional() {
        Row row = mock(Row.class);

        when(row.get(eq("PRODUCT_ID"), eq(Long.class))).thenReturn(null);
        when(row.get(eq("BRAND_ID"), eq(Long.class))).thenReturn(null);
        when(row.get(eq("PRICE_LIST"), eq(Long.class))).thenReturn(null);
        when(row.get(eq("START_DATE"), eq(LocalDateTime.class))).thenReturn(null);
        when(row.get(eq("END_DATE"), eq(LocalDateTime.class))).thenReturn(null);
        when(row.get(eq("PRICE"), eq(BigDecimal.class))).thenReturn(null);
        when(row.get(eq("CURRENCY"), eq(String.class))).thenReturn(null);
        when(row.get(eq("PRIORITY"), eq(Long.class))).thenReturn(null);

        ApplicablePrice price = new ApplicablePrice(
                Optional.ofNullable(row.get("PRODUCT_ID", Long.class)),
                Optional.ofNullable(row.get("BRAND_ID", Long.class)),
                Optional.ofNullable(row.get("PRICE_LIST", Long.class)),
                Optional.ofNullable(row.get("START_DATE", LocalDateTime.class)),
                Optional.ofNullable(row.get("END_DATE", LocalDateTime.class)),
                Optional.ofNullable(row.get("PRICE", BigDecimal.class)),
                Optional.ofNullable(row.get("CURRENCY", String.class)),
                Optional.ofNullable(row.get("PRIORITY", Long.class))
        );

        assertThat(price.productId()).isEmpty();
        assertThat(price.brandId()).isEmpty();
        assertThat(price.priceList()).isEmpty();
        assertThat(price.startDate()).isEmpty();
        assertThat(price.endDate()).isEmpty();
        assertThat(price.price()).isEmpty();
        assertThat(price.currency()).isEmpty();
        assertThat(price.priority()).isEmpty();
    }
}
