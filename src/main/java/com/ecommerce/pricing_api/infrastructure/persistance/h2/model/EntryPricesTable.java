package com.ecommerce.pricing_api.infrastructure.persistance.h2.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a single price entry in the H2 database.
 * <p>
 * Mapped to the "PRICES" table, this entity stores pricing information
 * for a specific product and brand, including validity period, priority,
 * and currency details.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("PRICES")
public class EntryPricesTable {

    /**
     * Unique identifier for the price entry (primary key).
     */
    @Id
    private Long id;

    /**
     * Identifier of the brand associated with this price entry.
     */
    private Long brandId;

    /**
     * Start date and time from which this price is valid.
     */
    private LocalDateTime startDate;

    /**
     * End date and time until which this price remains valid.
     */
    private LocalDateTime endDate;

    /**
     * Price list identifier, used to categorize pricing rules or promotions.
     */
    private Long priceList;

    /**
     * Identifier of the product to which this price applies.
     */
    private Long productId;

    /**
     * Priority level of this price entry; higher values take precedence when multiple prices overlap.
     */
    private Long priority;

    /**
     * Monetary value of the price.
     */
    private BigDecimal price;

    /**
     * ISO currency code for the price (e.g., "EUR", "USD").
     */
    private String currency;
}
