package com.ecommerce.pricing_api.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests for RedisProperties")
class RedisPropertiesTest {

    @Nested
    @DisplayName("POJO tests")
    class PojoTests {
        @Test
        @DisplayName("default values before setting")
        void defaults() {
            RedisProperties props = new RedisProperties();
            assertNull(props.getHost());
            assertEquals(0, props.getPort());
            assertEquals(0L, props.getTimeout());
            assertEquals(0L, props.getTtlSeconds());
        }

        @Test
        @DisplayName("setters and getters work correctly")
        void gettersAndSetters() {
            RedisProperties props = new RedisProperties();
            props.setHost("redis.local");
            props.setPort(6380);
            props.setTimeout(5_000L);
            props.setTtlSeconds(120L);

            assertEquals("redis.local", props.getHost());
            assertEquals(6380, props.getPort());
            assertEquals(5_000L, props.getTimeout());
            assertEquals(120L, props.getTtlSeconds());
        }
    }

    @Nested
    @DisplayName("Spring binding tests")
    class BindingTests {
        @EnableConfigurationProperties(RedisProperties.class)
        static class TestConfig {}

        private final ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(TestConfig.class);

        @Test
        @DisplayName("Should bind properties from spring.redis.*")
        void bindsFromProperties() {
            runner
                    .withPropertyValues(
                            "spring.redis.host=my-redis",
                            "spring.redis.port=6381",
                            "spring.redis.timeout=7500",
                            "spring.redis.ttl-seconds=300"
                    )
                    .run(ctx -> {
                        assertThat(ctx).hasSingleBean(RedisProperties.class);
                        RedisProperties props = ctx.getBean(RedisProperties.class);

                        assertThat(props.getHost()).isEqualTo("my-redis");
                        assertThat(props.getPort()).isEqualTo(6381);
                        assertThat(props.getTimeout()).isEqualTo(7_500L);
                        assertThat(props.getTtlSeconds()).isEqualTo(300L);
                    });
        }

        @Test
        @DisplayName("Should use default values when no properties are defined")
        void usesDefaultsWhenNoProperties() {
            runner
                    .run(ctx -> {
                        assertThat(ctx).hasSingleBean(RedisProperties.class);
                        RedisProperties props = ctx.getBean(RedisProperties.class);

                        assertThat(props.getHost()).isNull();
                        assertThat(props.getPort()).isZero();
                        assertThat(props.getTimeout()).isZero();
                        assertThat(props.getTtlSeconds()).isZero();
                    });
        }
    }
}
