package com.ecommerce.pricing_api.application.mapper;

import com.ecommerce.pricing_api.application.dto.ApplicablePriceResponseDto;
import com.ecommerce.pricing_api.domain.model.ApplicablePrice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApplicablePriceMapper Unit Tests")
class ApplicablePriceMapperTest {

    @Nested
    @DisplayName("toDto method")
    class ToDtoMethod {

        @Test
        @DisplayName("Should return empty Optional when input is empty")
        void shouldReturnEmptyWhenInputIsEmpty() {
            Optional<ApplicablePriceResponseDto> result = ApplicablePriceMapper.toDto(Optional.empty());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should correctly map all fields when all fields are present")
        void shouldMapAllFieldsCorrectly() {
            ApplicablePrice price = new ApplicablePrice(
                    Optional.of(35455L),
                    Optional.of(1L),
                    Optional.of(2L),
                    Optional.of(LocalDateTime.parse("2020-06-14T00:00:00")),
                    Optional.of(LocalDateTime.parse("2020-12-31T23:59:59")),
                    Optional.of(BigDecimal.valueOf(35.50)),
                    Optional.of("EUR"),
                    Optional.of(0L)
            );

            Optional<ApplicablePriceResponseDto> result = ApplicablePriceMapper.toDto(Optional.of(price));

            assertThat(result).isPresent();
            ApplicablePriceResponseDto dto = result.get();
            assertThat(dto.productId()).isEqualTo(35455L);
            assertThat(dto.brandId()).isEqualTo(1L);
            assertThat(dto.priceList()).isEqualTo(2L);
            assertThat(dto.startDate()).isEqualTo(LocalDateTime.parse("2020-06-14T00:00:00"));
            assertThat(dto.endDate()).isEqualTo(LocalDateTime.parse("2020-12-31T23:59:59"));
            assertThat(dto.price()).isEqualTo(BigDecimal.valueOf(35.50));
            assertThat(dto.currency()).isEqualTo("EUR");
        }

        @Test
        @DisplayName("Should map nulls when fields are absent")
        void shouldMapNullsWhenFieldsAreAbsent() {
            ApplicablePrice price = new ApplicablePrice(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            );

            Optional<ApplicablePriceResponseDto> result = ApplicablePriceMapper.toDto(Optional.of(price));

            assertThat(result).isPresent();
            ApplicablePriceResponseDto dto = result.get();
            assertThat(dto.productId()).isNull();
            assertThat(dto.brandId()).isNull();
            assertThat(dto.priceList()).isNull();
            assertThat(dto.startDate()).isNull();
            assertThat(dto.endDate()).isNull();
            assertThat(dto.price()).isNull();
            assertThat(dto.currency()).isNull();
        }
    }
}
