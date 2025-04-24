package com.inditex.pricing_api.application.repository;

import com.inditex.pricing_api.domain.model.ApplicablePrice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface PricesRepository {
    Mono<ApplicablePrice> findApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate);
}
