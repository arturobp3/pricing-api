package com.ecommerce.pricing_api.infrastructure.persistance.h2.repository;

import com.ecommerce.pricing_api.application.repository.PricesDatabaseRepository;
import com.ecommerce.pricing_api.domain.model.ApplicablePrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * H2-based implementation of {@link PricesDatabaseRepository} using R2DBC.
 * <p>
 * Executes SQL queries against the PRICES table to fetch pricing records
 * for a given product and brand, and maps the result set into the
 * {@link ApplicablePrice} domain model using {@link Optional} to safely handle potential nulls.
 * </p>
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class PricesH2DatabaseRepository implements PricesDatabaseRepository {

    private final DatabaseClient databaseClient;

    /**
     * Retrieves all price entries from the PRICES table for the specified
     * product and brand identifiers.
     * <p>
     * Executes a parameterized SQL query and maps each resulting row into an
     * {@link ApplicablePrice} instance wrapped with {@link Optional} to avoid {@code NullPointerException}.
     * </p>
     *
     * @param productId the identifier of the product to filter by
     * @param brandId   the identifier of the brand to filter by
     * @return a {@link Flux} emitting zero or more {@link ApplicablePrice}
     *         records matching the given criteria
     */
    @Override
    public Flux<ApplicablePrice> findAllByProductAndBrand(Long productId, Long brandId) {
        return databaseClient.sql("""
            SELECT PRODUCT_ID, BRAND_ID, PRICE_LIST, START_DATE, END_DATE, PRIORITY, PRICE, CURRENCY
            FROM PRICES
            WHERE PRODUCT_ID = :productId AND BRAND_ID = :brandId
        """)
                .bind("productId", productId)
                .bind("brandId", brandId)
                .map((row, metadata) -> new ApplicablePrice(
                        Optional.ofNullable(row.get("PRODUCT_ID", Long.class)),
                        Optional.ofNullable(row.get("BRAND_ID", Long.class)),
                        Optional.ofNullable(row.get("PRICE_LIST", Long.class)),
                        Optional.ofNullable(row.get("START_DATE", LocalDateTime.class)),
                        Optional.ofNullable(row.get("END_DATE", LocalDateTime.class)),
                        Optional.ofNullable(row.get("PRICE", BigDecimal.class)),
                        Optional.ofNullable(row.get("CURRENCY", String.class)),
                        Optional.ofNullable(row.get("PRIORITY", Long.class))
                ))
                .all()
                .doOnSubscribe(sub -> log.debug("Querying DB for productId={}, brandId={}", productId, brandId))
                .doOnComplete(() -> log.debug("Completed fetching prices for productId={}, brandId={}", productId, brandId));
    }
}
