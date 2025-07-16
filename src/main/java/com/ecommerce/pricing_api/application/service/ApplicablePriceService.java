package com.ecommerce.pricing_api.application.service;

import com.ecommerce.pricing_api.application.repository.PricesCacheRepository;
import com.ecommerce.pricing_api.application.repository.PricesDatabaseRepository;
import com.ecommerce.pricing_api.application.usecases.ApplicablePriceUseCase;
import com.ecommerce.pricing_api.domain.model.ApplicablePrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service implementation of {@link ApplicablePriceUseCase} that orchestrates
 * retrieval of price data from cache (Redis) or fallback to the H2 database,
 * applies date filtering and priority sorting, and returns the single
 * most applicable price for a given product, brand, and application date.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicablePriceService implements ApplicablePriceUseCase {

    private final PricesDatabaseRepository pricesDatabaseRepository;
    private final PricesCacheRepository pricesCacheRepository;

    /**
     * Retrieves the applicable price for the specified product and brand at the given date and time.
     * <ul>
     *     <li>Attempts to read a list of prices from the cache using a composite key.</li>
     *     <li>If the cache is empty, queries the database, stores the results in the cache, and then emits them.</li>
     *     <li>Filters the emitted prices to those whose validity interval includes {@code applicationDate}.</li>
     *     <li>Sorts the filtered prices by descending priority and picks the first (highest priority) element.</li>
     * </ul>
     *
     * @param productId       the identifier of the product to price
     * @param brandId         the identifier of the brand to price under
     * @param applicationDate the date and time at which the price should be applicable
     * @return a {@code Mono<Optional<ApplicablePrice>>} emitting the highest-priority price wrapped in an Optional,
     *         or an empty Optional if no price is found
     */
    @Override
    public Mono<Optional<ApplicablePrice>> getApplicablePrice(Long productId,
                                                              Long brandId,
                                                              LocalDateTime applicationDate) {
        String key = productId + ":" + brandId;
        log.info("Checking cache with key: {}", key);

        return pricesCacheRepository.find(key)
                .hasElements()
                .flatMapMany(hasCache -> {
                    if (hasCache) {
                        log.info("Cache hit for key: {}", key);
                        return pricesCacheRepository.find(key);
                    } else {
                        log.info("Cache miss. Querying H2 database for productId={} and brandId={}", productId, brandId);
                        return pricesDatabaseRepository.findAllByProductAndBrand(productId, brandId)
                                .collectList()
                                .flatMapMany(prices -> {
                                    if (prices.isEmpty()) {
                                        log.warn("No prices found in database for productId={} and brandId={}", productId, brandId);
                                        return Flux.empty();
                                    }
                                    log.info("Storing {} prices in Redis for key: {}", prices.size(), key);
                                    return pricesCacheRepository.save(key, prices)
                                            .thenMany(Flux.fromIterable(prices));
                                });
                    }
                })
                .filter(p ->
                        p.startDate().isPresent() &&
                                p.endDate().isPresent() &&
                                !applicationDate.isBefore(p.startDate().get()) &&
                                !applicationDate.isAfter(p.endDate().get())
                )
                .reduce((p1, p2) ->
                        p1.priority().orElse(0L) >= p2.priority().orElse(0L) ? p1 : p2
                )
                .map(Optional::of)
                .switchIfEmpty(Mono.just(Optional.empty()))
                .doOnNext(optPrice -> {
                    if (optPrice.isPresent()) {
                        log.info("Applicable price selected: {}", optPrice.get());
                    } else {
                        log.warn("No applicable price matched date filtering for productId={}, brandId={}, date={}",
                                productId, brandId, applicationDate);
                    }
                });
    }
}
