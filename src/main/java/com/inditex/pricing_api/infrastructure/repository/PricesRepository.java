package com.inditex.pricing_api.infrastructure.repository;

import com.inditex.pricing_api.infrastructure.h2.model.EntryPricesTable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PricesRepository extends ReactiveCrudRepository<EntryPricesTable, Long> {
}

