package com.ecommerce.pricing_api.infrastructure.persistance.redis.repository;

import com.ecommerce.pricing_api.application.repository.PricesCacheRepository;
import com.ecommerce.pricing_api.domain.model.ApplicablePrice;
import com.ecommerce.pricing_api.infrastructure.config.RedisProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Reactive Redis-based implementation of the {@link PricesCacheRepository}.
 * <p>
 * Provides methods to save and retrieve lists of {@link ApplicablePrice}
 * objects in Redis, using a time-to-live (TTL) configured via {@link RedisProperties}.
 * <p>
 * Each {@link ApplicablePrice} internally handles nullability using {@link Optional},
 * ensuring safe serialization and deserialization of possibly incomplete data.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PricesRedisRepository implements PricesCacheRepository {

    private final ReactiveRedisTemplate<String, List<ApplicablePrice>> redisTemplate;
    private final RedisProperties redisProperties;

    /**
     * Logs the Redis host and port configuration during bean initialization.
     * Useful for debugging container networking or property resolution issues.
     */
    @PostConstruct
    public void checkRedisConfig() {
        log.info("Redis config --> {}:{}", redisProperties.getHost(), redisProperties.getPort());
    }

    /**
     * Serializes and stores a list of {@link ApplicablePrice} objects under the given key,
     * with a time-to-live (TTL) as configured in {@link RedisProperties}.
     * <p>
     * The {@link ApplicablePrice} entries may contain optional fields, ensuring
     * robust handling of missing or partial data during cache operations.
     *
     * @param key    the Redis key under which to store the prices list
     * @param prices the list of {@link ApplicablePrice} instances to cache
     * @return a {@link Mono} that completes when the operation has finished successfully,
     *         or emits an error if serialization or Redis operation fails
     */
    @Override
    public Mono<Void> save(String key, List<ApplicablePrice> prices) {
        Duration ttl = Duration.ofSeconds(redisProperties.getTtlSeconds());
        return redisTemplate.opsForValue()
                .set(key, prices, ttl)
                .doOnSuccess(success -> log.debug("Serialized and saved {} prices to Redis with key: {}", prices.size(), key))
                .doOnError(e -> log.error("Failed to serialize prices to Redis for key: {}", key, e))
                .then();
    }

    /**
     * Retrieves and deserializes a cached list of {@link ApplicablePrice} objects
     * for the given key.
     * <p>
     * The retrieved {@link ApplicablePrice} instances are guaranteed to handle
     * missing fields via {@link Optional}, avoiding NullPointerExceptions downstream.
     *
     * @param key the Redis key whose associated prices list is to be retrieved
     * @return a {@link Flux} emitting each {@link ApplicablePrice} found under the key,
     *         or completing empty if the key is not present or on deserialization errors
     */
    @Override
    public Flux<ApplicablePrice> find(String key) {
        return redisTemplate.opsForValue()
                .get(key)
                .doOnNext(p -> log.debug("Found cache for key: {}", key))
                .doOnError(e -> log.error("Failed to deserialize Redis data for key: {}", key, e))
                .flatMapMany(Flux::fromIterable);
    }
}
