package com.ecommerce.pricing_api.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RedisConfigTest {

    private final RedisConfig redisConfig = new RedisConfig();

    @Test
    @DisplayName("Should create a reactive Redis connection factory")
    void shouldCreateReactiveRedisConnectionFactory() {
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setHost("localhost");
        redisProperties.setPort(6379);

        var factory = redisConfig.reactiveRedisConnectionFactory(redisProperties);

        assertThat(factory).isNotNull();
    }

    @Test
    @DisplayName("Should create a configured ReactiveRedisTemplate")
    void shouldCreateReactiveRedisTemplate() {
        ReactiveRedisConnectionFactory mockFactory = mock(ReactiveRedisConnectionFactory.class);
        ObjectMapper spyMapper = Mockito.spy(new ObjectMapper());

        var template = redisConfig.reactiveRedisTemplate(mockFactory, spyMapper);

        assertThat(template).isInstanceOf(ReactiveRedisTemplate.class);

        verify(spyMapper, atLeastOnce()).registerModule(any(JavaTimeModule.class));
        verify(spyMapper, atLeastOnce()).registerModule(any(Jdk8Module.class));
    }
}
