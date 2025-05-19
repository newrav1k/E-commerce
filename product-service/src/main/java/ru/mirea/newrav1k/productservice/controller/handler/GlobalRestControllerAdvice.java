package ru.mirea.newrav1k.productservice.controller.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mirea.newrav1k.productservice.exception.InventoryNotFoundException;
import ru.mirea.newrav1k.productservice.exception.ProductNotFoundException;

import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalRestControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleProductNotFoundException(ProductNotFoundException exception, Locale locale) {
        log.warn("Product not found: {}", exception.getMessage());
        return handleException(exception, HttpStatus.NOT_FOUND, locale);
    }

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleInventoryNotFoundException(InventoryNotFoundException exception, Locale locale) {
        log.warn("Inventory not found: {}", exception.getMessage());
        return handleException(exception, HttpStatus.NOT_FOUND, locale);
    }

    private ResponseEntity<ProblemDetail> handleException(Exception exception, HttpStatus status, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status,
                this.messageSource.getMessage(exception.getMessage(), new Object[0], exception.getMessage(), locale));
        return ResponseEntity.of(problemDetail).build();
    }

}