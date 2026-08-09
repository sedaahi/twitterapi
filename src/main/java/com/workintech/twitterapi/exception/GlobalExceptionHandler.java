package com.workintech.twitterapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TwitterException.class)
    public ResponseEntity<ApiError> handleTwitterException(TwitterException exception) {

        ApiError error = new ApiError(
                exception.getStatus().value(),
                exception.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, exception.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        ApiError error = new ApiError(
                400,
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    //Yanlış email/password gönderildiğinde authentication başarısızlığını exception olarak
    // ==>401 Unauthorized
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(
            BadCredentialsException exception
    ) {

        ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                "Email veya password hatalı.",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }
}