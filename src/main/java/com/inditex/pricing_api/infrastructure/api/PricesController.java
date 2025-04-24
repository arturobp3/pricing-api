package com.inditex.pricing_api.infrastructure.api;

import com.inditex.pricing_api.application.dto.ApplicablePriceResponseDto;
import com.inditex.pricing_api.application.mapper.ApplicablePriceMapper;
import com.inditex.pricing_api.application.usecases.ApplicablePriceUseCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    private final ApplicablePriceUseCase applicablePriceUseCase;

    @GetMapping
    public Mono<ResponseEntity<ApplicablePriceResponseDto>> getPrice(
            @RequestParam @NotNull @NotBlank
            @Pattern(
                    regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$"
            )
            String applicationDate,

            @RequestParam @NotNull Long productId,
            @RequestParam @NotNull Long brandId
    ) {
        //TODO: esto es necesario?
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime parsedDate = LocalDateTime.parse(applicationDate, formatter);

        return applicablePriceUseCase.getApplicablePrice(productId, brandId, parsedDate)
                .map(ApplicablePriceMapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }
}
