package com.ecommerce.pricing_api.infrastructure.persistance.redis.repository;

import com.ecommerce.pricing_api.domain.model.ApplicablePrice;
import com.ecommerce.pricing_api.infrastructure.config.RedisProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PricesRedisRepositoryTest {

    private PricesRedisRepository pricesRedisRepository;
    private ReactiveRedisTemplate<String, List<ApplicablePrice>> redisTemplate;
    private ReactiveValueOperations<String, List<ApplicablePrice>> valueOperations;
    private RedisProperties redisProperties;

    @BeforeEach
    void setUp() {
        redisTemplate = Mockito.mock(ReactiveRedisTemplate.class);
        valueOperations = Mockito.mock(ReactiveValueOperations.class);
        redisProperties = new RedisProperties();
        redisProperties.setHost("localhost");
        redisProperties.setPort(6379);
        redisProperties.setTtlSeconds(600);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        pricesRedisRepository = new PricesRedisRepository(redisTemplate, redisProperties);
    }

    @Test
    @DisplayName("Should save prices into Redis")
    void shouldSavePricesIntoRedis() {
        List<ApplicablePrice> prices = List.of(new ApplicablePrice(
                Optional.of(1L),
                Optional.of(1L),
                Optional.of(1L),
                Optional.of(LocalDateTime.now()),
                Optional.of(LocalDateTime.now()),
                Optional.of(BigDecimal.valueOf(50)),
                Optional.of("EUR"),
                Optional.of(1L)
        ));

        when(valueOperations.set(any(), any(), any())).thenReturn(Mono.just(true));

        StepVerifier.create(pricesRedisRepository.save("test-key", prices))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find prices from Redis")
    void shouldFindPricesFromRedis() {
        List<ApplicablePrice> prices = List.of(new ApplicablePrice(
                Optional.of(1L),
                Optional.of(1L),
                Optional.of(1L),
                Optional.of(LocalDateTime.now()),
                Optional.of(LocalDateTime.now()),
                Optional.of(BigDecimal.valueOf(50)),
                Optional.of("EUR"),
                Optional.of(1L)
        ));

        when(valueOperations.get(any())).thenReturn(Mono.just(prices));

        StepVerifier.create(pricesRedisRepository.find("test-key"))
                .expectNextMatches(price -> price.productId().isPresent())
                .verifyComplete();
    }
}
