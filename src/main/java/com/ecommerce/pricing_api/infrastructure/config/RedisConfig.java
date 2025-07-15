package com.ecommerce.pricing_api.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module; // <- necesario para Optional<>
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ecommerce.pricing_api.domain.model.ApplicablePrice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

/**
 * Spring configuration class for setting up Redis serialization and connection.
 * <p>
 * Provides a {@link ReactiveRedisTemplate} bean that uses String keys and JSON-serialized
 * lists of {@link ApplicablePrice} as values. Also explicitly configures the
 * {@link ReactiveRedisConnectionFactory} using {@link RedisProperties}.
 * <p>
 * The {@link ObjectMapper} is customized to handle {@link java.util.Optional}
 * and Java 8 date/time types.
 */
@Configuration
public class RedisConfig {

    /**
     * Creates a reactive Redis connection factory using host and port from {@link RedisProperties}.
     *
     * @param redisProperties the properties with host and port configuration
     * @return the reactive Redis connection factory
     */
    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        return new LettuceConnectionFactory(config);
    }

    /**
     * Creates and configures a {@link ReactiveRedisTemplate} for Redis operations.
     * <p>
     * Keys are serialized as plain strings, while values (lists of {@link ApplicablePrice})
     * are serialized to and from JSON using Jackson.
     * <p>
     * The provided {@link ObjectMapper} is enhanced with the {@link Jdk8Module} for {@link java.util.Optional}
     * support, and the {@link JavaTimeModule} for Java 8 date/time support. Timestamps are written in ISO format.
     *
     * @param factory      the reactive Redis connection factory
     * @param objectMapper the Jackson object mapper for JSON serialization
     * @return a reactive Redis template for String keys and List&lt;ApplicablePrice&gt; values
     */
    @Bean
    public ReactiveRedisTemplate<String, List<ApplicablePrice>> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory,
            ObjectMapper objectMapper
    ) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module()); // <-- Muy importante para Optional
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RedisSerializer<String> keySerializer = new StringRedisSerializer();

        Jackson2JsonRedisSerializer<List<ApplicablePrice>> valueSerializer =
                new Jackson2JsonRedisSerializer<>(
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ApplicablePrice.class)
                );
        valueSerializer.setObjectMapper(objectMapper);

        RedisSerializationContext<String, List<ApplicablePrice>> context = RedisSerializationContext
                .<String, List<ApplicablePrice>>newSerializationContext(keySerializer)
                .value(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
