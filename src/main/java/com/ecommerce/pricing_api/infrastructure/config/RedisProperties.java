package com.ecommerce.pricing_api.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Redis cache behavior.
 * <p>
 * Binds to properties prefixed with {@code spring.redis} in the application configuration.
 * Includes the host, port, connection timeout, and TTL for cached price entries.
 */
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

    /**
     * Redis server hostname or IP address.
     */
    private String host;

    /**
     * Redis server port number.
     */
    private int port;

    /**
     * Connection timeout (in milliseconds).
     */
    private long timeout;

    /**
     * Time-to-live for Redis cache entries, in seconds.
     */
    private long ttlSeconds;

    /**
     * Gets the Redis server hostname or IP.
     *
     * @return the Redis host
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the Redis server hostname or IP.
     *
     * @param host the Redis host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets the Redis server port number.
     *
     * @return the Redis port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the Redis server port number.
     *
     * @param port the Redis port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Gets the connection timeout in milliseconds.
     *
     * @return the timeout value
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Sets the connection timeout in milliseconds.
     *
     * @param timeout the timeout value
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets the time-to-live (TTL) for Redis cache entries in seconds.
     *
     * @return the TTL in seconds
     */
    public long getTtlSeconds() {
        return ttlSeconds;
    }

    /**
     * Sets the time-to-live (TTL) for Redis cache entries in seconds.
     *
     * @param ttlSeconds the TTL in seconds
     */
    public void setTtlSeconds(long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }
}
