package com.ecommerce.pricing_api.application.repository;

import com.ecommerce.pricing_api.domain.model.ApplicablePrice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Repository abstraction for caching {@link ApplicablePrice} data.
 * <p>
 * Provides reactive methods to save and retrieve lists of applicable prices
 * using a simple key-based cache.
 * </p>
 * <p>
 * Each {@link ApplicablePrice} instance internally uses {@link java.util.Optional}
 * for handling nullable fields, ensuring safe consumption of possibly incomplete data.
 * </p>
 */
public interface PricesCacheRepository {

    /**
     * Saves a list of {@link ApplicablePrice} entries in the cache under the given key.
     * <p>
     * The cached entries may contain optional fields.
     * </p>
     *
     * @param key    the cache key under which to store the price list
     * @param prices the list of {@link ApplicablePrice} objects to cache
     * @return a {@link Mono} that completes when the save operation is done,
     *         or emits an error if the operation fails
     */
    Mono<Void> save(String key, List<ApplicablePrice> prices);

    /**
     * Retrieves a list of {@link ApplicablePrice} entries from the cache for the given key.
     * <p>
     * The retrieved {@link ApplicablePrice} instances may have empty optional fields
     * depending on the stored data.
     * </p>
     *
     * @param key the cache key whose associated price list should be fetched
     * @return a {@link Flux} emitting each {@link ApplicablePrice} found under the key,
     *         or completing empty if no data is found or an error occurs
     */
    Flux<ApplicablePrice> find(String key);
}
