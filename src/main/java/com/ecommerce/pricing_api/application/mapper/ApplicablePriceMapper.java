package com.ecommerce.pricing_api.application.mapper;

import com.ecommerce.pricing_api.application.dto.ApplicablePriceResponseDto;
import com.ecommerce.pricing_api.domain.model.ApplicablePrice;

import java.util.Optional;

/**
 * Mapper utility for converting domain {@link ApplicablePrice} instances
 * into transport-friendly {@link ApplicablePriceResponseDto} instances.
 */
public class ApplicablePriceMapper {

    /**
     * Transforms an {@link ApplicablePrice} domain object into an
     * {@link Optional} of {@link ApplicablePriceResponseDto} suitable for API responses.
     * <p>
     * If the provided {@link Optional} is empty, this method returns an empty {@link Optional}.
     * Otherwise, maps all non-null fields, providing sensible defaults where necessary.
     * </p>
     *
     * @param price the {@link Optional} domain object containing detailed pricing information
     * @return an {@link Optional} containing the corresponding DTO, or empty if no price was provided
     */
    public static Optional<ApplicablePriceResponseDto> toDto(Optional<ApplicablePrice> price) {
        return price.map(p -> new ApplicablePriceResponseDto(
                p.productId().orElse(null),
                p.brandId().orElse(null),
                p.priceList().orElse(null),
                p.startDate().orElse(null),
                p.endDate().orElse(null),
                p.price().orElse(null),
                p.currency().orElse(null)
        ));
    }
}
