package com.ecommerce.pricing_api.infrastructure.persistance.h2.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EntryPricesTableTest {

    @Test
    void shouldCreateEntryUsingAllArgsConstructor() {
        EntryPricesTable entry = new EntryPricesTable(
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                2L,
                35455L,
                1L,
                BigDecimal.valueOf(35.50),
                "EUR"
        );

        assertThat(entry).isNotNull();
        assertThat(entry.getId()).isEqualTo(1L);
        assertThat(entry.getBrandId()).isEqualTo(1L);
        assertThat(entry.getPriceList()).isEqualTo(2L);
        assertThat(entry.getProductId()).isEqualTo(35455L);
        assertThat(entry.getPriority()).isEqualTo(1L);
        assertThat(entry.getPrice()).isEqualByComparingTo("35.50");
        assertThat(entry.getCurrency()).isEqualTo("EUR");
    }

    @Test
    void shouldCreateEntryUsingBuilder() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusHours(1);

        EntryPricesTable entry = EntryPricesTable.builder()
                .id(1L)
                .brandId(1L)
                .startDate(startDate)
                .endDate(endDate)
                .priceList(2L)
                .productId(35455L)
                .priority(1L)
                .price(BigDecimal.valueOf(25.45))
                .currency("USD")
                .build();

        assertThat(entry).isNotNull();
        assertThat(entry.getStartDate()).isEqualTo(startDate);
        assertThat(entry.getEndDate()).isEqualTo(endDate);
    }

    @Test
    void shouldTestEqualsAndHashCode() {
        EntryPricesTable entry1 = new EntryPricesTable();
        EntryPricesTable entry2 = new EntryPricesTable();

        assertThat(entry1)
                .isEqualTo(entry2)
                .hasSameHashCodeAs(entry2);
    }

    @Test
    void shouldHaveMeaningfulToString() {
        EntryPricesTable entry = new EntryPricesTable();
        String toString = entry.toString();

        assertThat(toString).contains("EntryPricesTable");
    }
}
