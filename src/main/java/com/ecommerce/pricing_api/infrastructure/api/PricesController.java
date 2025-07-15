package com.ecommerce.pricing_api.infrastructure.api;

import com.ecommerce.pricing_api.application.dto.ApplicablePriceResponseDto;
import com.ecommerce.pricing_api.application.mapper.ApplicablePriceMapper;
import com.ecommerce.pricing_api.application.usecases.ApplicablePriceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * REST controller for handling price retrieval requests.
 * <p>
 * Exposes an endpoint to obtain the applicable price for a given product and brand at a specified date and time.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
@Validated
public class PricesController {

    private final ApplicablePriceUseCase applicablePriceUseCase;

    /**
     * Retrieves the applicable price for a given product and brand at the specified application date.
     *
     * @param applicationDate the date and time for which the price should be calculated (must be in ISO date-time format)
     * @param productId       the identifier of the product
     * @param brandId         the identifier of the brand
     * @return a {@link Mono} emitting a {@link ResponseEntity} containing the {@link ApplicablePriceResponseDto} if a price is found,
     *         or a 204 No Content response if no applicable price exists
     */
    @Operation(
            summary = "Get applicable price",
            description = "Returns the price list applicable to a given product and brand at a specific date"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price found"),
            @ApiResponse(responseCode = "204", description = "No price applicable"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public Mono<ResponseEntity<ApplicablePriceResponseDto>> getPrice(
            @Parameter(description = "Application date in ISO-8601 format", example = "2020-06-14T10:00:00")
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime applicationDate,

            @Parameter(description = "Product ID", example = "35455")
            @RequestParam @NotNull Long productId,

            @Parameter(description = "Brand ID", example = "1")
            @RequestParam @NotNull Long brandId
    ) {
        log.info("Received request -> applicationDate: {}, productId: {}, brandId: {}",
                applicationDate, productId, brandId);

        return applicablePriceUseCase.getApplicablePrice(productId, brandId, applicationDate)
                .flatMap(price -> ApplicablePriceMapper.toDto(price)
                        .map(dto -> Mono.just(ResponseEntity.ok(dto)))
                        .orElseGet(() -> Mono.just(ResponseEntity.noContent().build()))
                );
    }
}
