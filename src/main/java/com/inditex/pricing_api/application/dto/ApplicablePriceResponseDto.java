package com.inditex.pricing_api.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApplicablePriceResponseDto(
        Long productId,
        Long brandId,
        Long priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price,
        String currency
) {}
