package ru.mirea.newrav1k.userservice.controller.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mirea.newrav1k.userservice.exception.UserAlreadyExistException;
import ru.mirea.newrav1k.userservice.exception.UserNotFoundException;

import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class UserExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFoundException(UserNotFoundException exception, Locale locale) {
        log.error("Handling UserNotFoundException", exception);
        return handleException(exception, HttpStatus.NOT_FOUND, locale);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyExistException(UserAlreadyExistException exception, Locale locale) {
        log.error("Handling UserAlreadyExistException", exception);
        return handleException(exception, HttpStatus.CONFLICT, locale);
    }

    private ResponseEntity<ProblemDetail> handleException(Exception exception, HttpStatus status, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status,
                this.messageSource.getMessage(exception.getMessage(), new Object[0], exception.getMessage(), locale));
        return ResponseEntity.of(problemDetail).build();
    }

}