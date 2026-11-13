package com.example.user_login_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

  @ExceptionHandler(AccountAlreadyExistsException.class)
  public ResponseEntity<Map<String, String>> handleDuplicateEmail(
    AccountAlreadyExistsException exception){

    return ResponseEntity.status(HttpStatus.CONFLICT)
      .body(Map.of("message", exception.getMessage()));
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<Map<String, String>> handleInvalidCredentials(
    InvalidCredentialsException exception){

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
      .body(Map.of("message", exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationErrors(
    MethodArgumentNotValidException exception) {

    String message = exception.getBindingResult()
      .getFieldErrors()
      .stream()
      .findFirst()
      .map(error -> error.getField() + ": "
        + error.getDefaultMessage())
      .orElse("Invalid request");

    return ResponseEntity.badRequest()
      .body(Map.of("message", message));
  }
}
