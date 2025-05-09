package ru.newrav1k.github.orderservice.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.newrav1k.github.orderservice.exception.ItemNotFoundException;
import ru.newrav1k.github.orderservice.exception.OrderNotFoundException;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalRestControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleOrderNotFoundException(OrderNotFoundException exception, Locale locale) {
        return handleException(exception, HttpStatus.NOT_FOUND, locale);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleItemNotFoundException(ItemNotFoundException exception, Locale locale) {
        return handleException(exception, HttpStatus.NOT_FOUND, locale);
    }

    private ResponseEntity<ProblemDetail> handleException(Exception exception, HttpStatus status, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status,
                this.messageSource.getMessage(exception.getMessage(), new Object[0], exception.getMessage(), locale));
        return ResponseEntity.of(problemDetail).build();
    }

}