package com.inditex.pricing_api.application.usecases;

import com.inditex.pricing_api.domain.model.ApplicablePrice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ApplicablePriceUseCase {
    Mono<ApplicablePrice> getApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate);
}