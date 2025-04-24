package com.inditex.pricing_api.application.mapper;

import com.inditex.pricing_api.application.dto.ApplicablePriceResponseDto;
import com.inditex.pricing_api.domain.model.ApplicablePrice;

public class ApplicablePriceMapper {
    public static ApplicablePriceResponseDto toDto(ApplicablePrice price) {
        return new ApplicablePriceResponseDto(
                price.productId(),
                price.brandId(),
                price.priceList(),
                price.startDate(),
                price.endDate(),
                price.price(),
                price.currency()
        );
    }
}
