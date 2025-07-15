package com.ecommerce.pricing_api.infrastructure.persistance.h2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecommerce.pricing_api.infrastructure.persistance.h2.model.EntryPricesTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.FetchSpec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class H2InitializerTest {

    private ObjectMapper objectMapper;
    private DatabaseClient databaseClient;
    private H2Initializer h2Initializer;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        databaseClient = mock(DatabaseClient.class);
        h2Initializer = new H2Initializer(objectMapper, databaseClient);
    }

    @Nested
    class InitTests {

        @Test
        @DisplayName("Should initialize database successfully")
        void shouldInitializeDatabaseSuccessfully() {
            H2Initializer spyInitializer = spy(new H2Initializer(objectMapper, databaseClient));

            doReturn(Mono.just("CREATE TABLE PRICES")).when(spyInitializer).readSqlFromClasspath(anyString());
            doReturn(Mono.empty()).when(spyInitializer).createPricesTableIfNotExists(anyString());
            doReturn(Mono.just(List.of())).when(spyInitializer).loadEntriesFromJson(anyString());
            doReturn(Mono.empty()).when(spyInitializer).insertEntries(any());

            spyInitializer.init();

            verify(spyInitializer).readSqlFromClasspath(anyString());
            verify(spyInitializer).createPricesTableIfNotExists(anyString());
            verify(spyInitializer).loadEntriesFromJson(anyString());
            verify(spyInitializer).insertEntries(any());
        }
    }

    @Nested
    class ReadSqlFromClasspathTests {

        @Test
        @DisplayName("Should read SQL file successfully")
        void shouldReadSqlFileSuccessfully() {
            StepVerifier.create(h2Initializer.readSqlFromClasspath("schema.sql"))
                    .expectNextMatches(sql -> sql.contains("CREATE TABLE") || sql.length() > 0)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should fail if SQL file not found")
        void shouldFailIfSqlFileNotFound() {
            StepVerifier.create(h2Initializer.readSqlFromClasspath("nonexistent.sql"))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                            throwable.getMessage().contains("Could not read SQL"))
                    .verify();
        }
    }

    @Nested
    class CreatePricesTableIfNotExistsTests {

        @Test
        @DisplayName("Should create table successfully")
        void shouldCreateTableSuccessfully() {
            DatabaseClient.GenericExecuteSpec executeSpec = mock(DatabaseClient.GenericExecuteSpec.class);
            FetchSpec<Map<String, Object>> fetchSpec = mock(FetchSpec.class);

            when(databaseClient.sql(anyString())).thenReturn(executeSpec);
            when(executeSpec.fetch()).thenReturn(fetchSpec);
            when(fetchSpec.rowsUpdated()).thenReturn(Mono.just(1L));

            StepVerifier.create(h2Initializer.createPricesTableIfNotExists("CREATE TABLE test"))
                    .verifyComplete();

            verify(databaseClient).sql(anyString());
            verify(executeSpec).fetch();
            verify(fetchSpec).rowsUpdated();
        }
    }

    @Nested
    class LoadEntriesFromJsonTests {

        @Test
        @DisplayName("Should fail to load entries if file not found")
        void shouldFailIfJsonNotFound() {
            StepVerifier.create(h2Initializer.loadEntriesFromJson("data/prod/es/nonexistent.json"))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                            throwable.getMessage().contains("Could not load JSON"))
                    .verify();
        }
    }

    @Nested
    class InsertEntriesTests {

        @Test
        @DisplayName("Should insert entries successfully")
        void shouldInsertEntriesSuccessfully() {
            EntryPricesTable entry = EntryPricesTable.builder()
                    .id(1L)
                    .brandId(1L)
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusDays(1))
                    .priceList(1L)
                    .productId(35455L)
                    .priority(0L)
                    .price(BigDecimal.valueOf(50.0))
                    .currency("EUR")
                    .build();

            DatabaseClient.GenericExecuteSpec executeSpec = mock(DatabaseClient.GenericExecuteSpec.class);
            FetchSpec<Map<String, Object>> fetchSpec = mock(FetchSpec.class);

            when(databaseClient.sql(anyString())).thenReturn(executeSpec);
            when(executeSpec.bind(anyString(), any())).thenReturn(executeSpec);
            when(executeSpec.fetch()).thenReturn(fetchSpec);
            when(fetchSpec.rowsUpdated()).thenReturn(Mono.just(1L));

            StepVerifier.create(h2Initializer.insertEntries(List.of(entry)))
                    .verifyComplete();

            verify(databaseClient, atLeastOnce()).sql(anyString());
            verify(executeSpec, atLeast(1)).bind(anyString(), any());
            verify(fetchSpec, atLeastOnce()).rowsUpdated();
        }
    }
}
