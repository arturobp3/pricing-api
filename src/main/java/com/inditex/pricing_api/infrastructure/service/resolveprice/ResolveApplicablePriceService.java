package com.inditex.pricing_api.infrastructure.service.resolveprice;

import com.inditex.pricing_api.domain.service.resolveprice.ResolveApplicablePriceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResolveApplicablePriceService implements ResolveApplicablePriceUseCase {

    @Override
    public Mono<Void> resolve(LocalDateTime applicationDate, Long productId, Long brandId) {
        System.out.println("→ Resolviendo precio para:");
        System.out.println("  Fecha: " + applicationDate.toString());
        System.out.println("  Producto: " + productId);
        System.out.println("  Marca: " + brandId);
        return Mono.empty();
    }
}