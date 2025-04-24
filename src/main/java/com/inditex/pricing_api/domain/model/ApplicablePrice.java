package com.inditex.pricing_api.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApplicablePrice(
        Long productId,
        Long brandId,
        Long priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price,
        String currency
) {}
