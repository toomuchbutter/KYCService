package com.springBootProject.unifiedBankingSystem.KYC.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(Exception e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(DuplicatePanException.class)
    public ResponseEntity<String> handleDuplicate(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<String> handleInvalid(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
