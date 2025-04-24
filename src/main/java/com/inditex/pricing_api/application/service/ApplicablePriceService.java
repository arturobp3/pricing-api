package com.inditex.pricing_api.application.service;

import com.inditex.pricing_api.application.repository.PricesRepository;
import com.inditex.pricing_api.application.usecases.ApplicablePriceUseCase;
import com.inditex.pricing_api.domain.model.ApplicablePrice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApplicablePriceService implements ApplicablePriceUseCase {

    private final PricesRepository pricesRepository;

    @Override
    public Mono<ApplicablePrice> getApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate) {
        return pricesRepository.findApplicablePrice(productId, brandId, applicationDate);
    }
}