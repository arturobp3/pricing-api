package com.ecommerce.pricing_api.infrastructure.api.exceptions;

import com.ecommerce.pricing_api.application.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.time.LocalDateTime;

/**
 * Global exception handler for the Pricing API.
 * <p>
 * Captures and handles exceptions thrown by the application, transforming them
 * into consistent and descriptive error responses for the API consumers.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation exceptions caused by invalid request parameters.
     *
     * @param ex the thrown validation exception
     * @return a 400 Bad Request response with error details
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(WebExchangeBindException ex) {
        String errorMessage = ex.getAllErrors().get(0).getDefaultMessage();

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                errorMessage,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    /**
     * Handles exceptions related to invalid input format or type.
     *
     * @param ex the thrown input exception
     * @return a 400 Bad Request response with error details
     */
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponseDto> handleInputException(ServerWebInputException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getReason(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    /**
     * Handles cases where no applicable price is found, returning a 404 response.
     *
     * @param ex the thrown PriceNotFoundException
     * @return a 404 Not Found response with error details
     */
    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlePriceNotFoundException(PriceNotFoundException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    /**
     * Handles any other unexpected exceptions.
     *
     * @param ex the thrown exception
     * @return a 500 Internal Server Error response with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }
}
