package com.ecommerce.pricing_api.application.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing the standardized error response payload for API requests.
 * <p>
 * Encapsulates relevant details about an error that occurred while processing a request,
 * providing clients with consistent information about the HTTP status, error type,
 * descriptive message, and timestamp of the error occurrence.
 * </p>
 *
 * @param status    the HTTP status code associated with the error (e.g., 400, 404, 500)
 * @param error     the short, human-readable error type (e.g., "Bad Request", "Not Found")
 * @param message   a descriptive error message explaining the reason for the failure
 * @param timestamp the timestamp indicating when the error occurred, in ISO-8601 format
 */
public record ErrorResponseDto(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {}
