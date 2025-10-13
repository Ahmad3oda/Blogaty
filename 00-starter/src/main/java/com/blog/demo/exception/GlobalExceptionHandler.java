package com.blog.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorMessage> handleBlogException(GlobalException ex){
        ErrorMessage error = new ErrorMessage();
        error.setMessage(ex.getMessage());
        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
        error.setTimestamp(Instant.now().toString());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
