package com.inditex.pricing_api.infrastructure.persistance.h2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.pricing_api.infrastructure.persistance.h2.model.EntryPricesTable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class H2Initializer {

    private final ObjectMapper objectMapper;
    private final DatabaseClient databaseClient;

    @PostConstruct
    public void init() {
        log.info("Starting database initialization...");

        String env = System.getenv().getOrDefault("APP_ENV", "prod");
        String region = System.getenv().getOrDefault("APP_REGION", "es");
        String jsonPath = String.format("data/%s/%s/entries.json", env, region);
        String ddlPath = "schema.sql";

        readSqlFromClasspath(ddlPath)
                .flatMap(this::createPricesTableIfNotExists)
                .doOnSuccess(unused -> log.info("Prices table created or already exists"))
                .then(loadEntriesFromJson(jsonPath))
                .doOnNext(entries -> log.info("Loaded {} entries from JSON", entries.size()))
                .flatMap(this::insertEntries)
                .doOnSuccess(unused -> log.info("Entries inserted successfully"))
                .doOnError(e -> log.error("Database initialization failed", e))
                .subscribe();
    }

    private Mono<String> readSqlFromClasspath(String filename) {
        try {
            ClassPathResource resource = new ClassPathResource(filename);
            InputStream inputStream = resource.getInputStream();
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return Mono.just(content);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Could not read SQL from classpath: " + filename, e));
        }
    }

    private Mono<Void> createPricesTableIfNotExists(String sql) {
        return databaseClient.sql(sql)
                .fetch()
                .rowsUpdated()
                .doOnNext(count -> log.info("SQL instruction executed, affected rows: {}", count))
                .then();
    }

    private Mono<List<EntryPricesTable>> loadEntriesFromJson(String path) {
        try {
            var inputStream = getClass().getClassLoader().getResourceAsStream(path);
            if (inputStream == null) {
                throw new RuntimeException("File not found in classpath: " + path);
            }
            List<EntryPricesTable> entries = objectMapper.readValue(inputStream, new TypeReference<>() {});
            return Mono.just(entries);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Could not load JSON file: " + path, e));
        }
    }

    private Mono<Void> insertEntries(List<EntryPricesTable> entries) {
        return Flux.fromIterable(entries)
                .flatMap(entry -> databaseClient.sql("""
                INSERT INTO PRICES (ID, BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURRENCY)
                VALUES (:id, :brandId, :startDate, :endDate, :priceList, :productId, :priority, :price, :currency)
            """)
                        .bind("id", entry.getId())
                        .bind("brandId", entry.getBrandId())
                        .bind("startDate", entry.getStartDate())
                        .bind("endDate", entry.getEndDate())
                        .bind("priceList", entry.getPriceList())
                        .bind("productId", entry.getProductId())
                        .bind("priority", entry.getPriority())
                        .bind("price", entry.getPrice())
                        .bind("currency", entry.getCurrency())
                        .fetch()
                        .rowsUpdated()
                        .doOnNext(count -> log.debug("Inserted entry with ID {} ({} row(s) affected)", entry.getId(), count))
                )
                .then();
    }
}
