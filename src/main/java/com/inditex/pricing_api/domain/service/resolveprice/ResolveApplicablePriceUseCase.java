package com.inditex.pricing_api.domain.service.resolveprice;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ResolveApplicablePriceUseCase {
    Mono<Void> resolve(LocalDateTime applicationDate, Long productId, Long brandId);
}