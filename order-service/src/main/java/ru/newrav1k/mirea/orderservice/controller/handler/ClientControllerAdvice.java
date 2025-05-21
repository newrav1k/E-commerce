package ru.newrav1k.mirea.orderservice.controller.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.newrav1k.mirea.orderservice.exception.ProductClientException;

import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ClientControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(ProductClientException.class)
    public ResponseEntity<ProblemDetail> handleProductConnectException(ProductClientException exception, Locale locale) {
        log.error("Product connect exception");
        return handleException(exception, HttpStatus.BAD_GATEWAY, locale);
    }

    private ResponseEntity<ProblemDetail> handleException(Exception exception, HttpStatus status, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status,
                this.messageSource.getMessage(exception.getMessage(), new Object[0], exception.getMessage(), locale));
        return ResponseEntity.of(problemDetail).build();
    }

}