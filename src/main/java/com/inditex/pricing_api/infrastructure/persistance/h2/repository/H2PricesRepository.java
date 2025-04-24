package com.inditex.pricing_api.infrastructure.persistance.h2.repository;

import com.inditex.pricing_api.application.repository.PricesRepository;
import com.inditex.pricing_api.domain.model.ApplicablePrice;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class H2PricesRepository implements PricesRepository {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<ApplicablePrice> findApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate) {
        return databaseClient.sql("""
                SELECT PRODUCT_ID, BRAND_ID, PRICE_LIST, START_DATE, END_DATE, PRICE, CURRENCY
                FROM PRICES
                WHERE PRODUCT_ID = :productId
                  AND BRAND_ID = :brandId
                  AND :applicationDate BETWEEN START_DATE AND END_DATE
                ORDER BY PRIORITY DESC
                LIMIT 1
            """)
                .bind("productId", productId)
                .bind("brandId", brandId)
                .bind("applicationDate", applicationDate)
                .map((row, rowMetadata) -> new ApplicablePrice(
                        row.get("PRODUCT_ID", Long.class),
                        row.get("BRAND_ID", Long.class),
                        row.get("PRICE_LIST", Long.class),
                        row.get("START_DATE", LocalDateTime.class),
                        row.get("END_DATE", LocalDateTime.class),
                        row.get("PRICE", BigDecimal.class),
                        row.get("CURRENCY", String.class)
                ))
                .one();
    }
}