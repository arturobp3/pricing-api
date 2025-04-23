package com.inditex.pricing_api.infrastructure.controller;

import com.inditex.pricing_api.domain.service.resolveprice.ResolveApplicablePriceUseCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
@Validated
public class PricesController {

    private final ResolveApplicablePriceUseCase resolveApplicablePriceUseCase;

    @GetMapping
    public Mono<Void> getPrice(
            @RequestParam @NotNull @NotBlank
            @Pattern(
                    regexp = "^\\d{4}-\\d{2}-\\d{2}-\\d{2}\\.\\d{2}\\.\\d{2}$"
            )
            String applicationDate,

            @RequestParam @NotNull
            Long productId,

            @RequestParam @NotNull
            Long brandId
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss");
        LocalDateTime parsedDate = LocalDateTime.parse(applicationDate, formatter);
        return resolveApplicablePriceUseCase.resolve(parsedDate, productId, brandId);
    }
}
