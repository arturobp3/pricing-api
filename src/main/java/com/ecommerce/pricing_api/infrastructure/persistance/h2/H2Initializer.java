package com.ecommerce.pricing_api.infrastructure.persistance.h2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecommerce.pricing_api.infrastructure.persistance.h2.model.EntryPricesTable;
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

/**
 * Component responsible for initializing the H2 database schema and populating it with initial data.
 * <p>
 * On application startup, it executes DDL to create the PRICES table (if not exists) and loads
 * JSON entries defined under classpath paths depending on environment and region.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class H2Initializer {

    private final ObjectMapper objectMapper;
    private final DatabaseClient databaseClient;

    /**
     * Entry point called after bean construction to initialize database schema and data.
     */
    @PostConstruct
    public void init() {
        log.info("Starting database initialization...");

        String env = System.getenv().getOrDefault("APP_ENV", "prod");
        String region = System.getenv().getOrDefault("APP_REGION", "es");
        String jsonPath = String.format("data/%s/%s/large_entries.json", env, region);
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

    /**
     * Reads a SQL script file from the classpath.
     *
     * @param filename the classpath resource name of the SQL file
     * @return Mono emitting the file content as a String
     */
    Mono<String> readSqlFromClasspath(String filename) {
        try {
            ClassPathResource resource = new ClassPathResource(filename);
            InputStream inputStream = resource.getInputStream();
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return Mono.just(content);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Could not read SQL from classpath: " + filename, e));
        }
    }

    /**
     * Executes the provided SQL to create the PRICES table if it does not exist.
     *
     * @param sql DDL statement(s) to execute
     * @return Mono completing when execution finishes
     */
    Mono<Void> createPricesTableIfNotExists(String sql) {
        return databaseClient.sql(sql)
                .fetch()
                .rowsUpdated()
                .doOnNext(count -> log.info("SQL instruction executed, affected rows: {}", count))
                .then();
    }

    /**
     * Loads a list of {@link EntryPricesTable} objects from a JSON file on the classpath.
     *
     * @param path classpath-relative JSON file path
     * @return Mono emitting the parsed list of entries
     */
    Mono<List<EntryPricesTable>> loadEntriesFromJson(String path) {
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

    /**
     * Inserts a list of {@link EntryPricesTable} entries into the PRICES table.
     *
     * @param entries list of table entry objects to insert
     * @return Mono completing when all inserts have been processed
     */
    Mono<Void> insertEntries(List<EntryPricesTable> entries) {
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
