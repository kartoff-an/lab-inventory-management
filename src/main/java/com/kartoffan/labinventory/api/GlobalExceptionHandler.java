package com.kartoffan.labinventory.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.security.access.AccessDeniedException;

import com.kartoffan.labinventory.exception.ApiException;
import com.kartoffan.labinventory.exception.InsufficientStockException;
import com.kartoffan.labinventory.exception.InvalidCredentialsException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<?> handleApiException(ApiException exception) {
    ApiError error = ApiError.builder()
        .code(exception.getCode())
        .message(exception.getMessage())
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity
        .status(exception.getStatus())
        .body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException exception) {
    List<String> details = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.toList());

    ApiError error = ApiError.builder()
        .code("VALIDATION_ERROR")
        .message("Invalid request data")
        .details(details)
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity
        .badRequest()
        .body(error);
  }
  
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException exception) {
    ApiError error = ApiError.builder()
        .code("CONSTRAINT_VIOLATION")
        .message(exception.getMessage())
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity
        .badRequest()
        .body(error);
  }
  
  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException exception) {
    ApiError error = ApiError.builder()
        .code("INVALID_CREDENTIALS")
        .message(exception.getMessage())
        .timestamp(LocalDateTime.now())
        .build();
        
    return ResponseEntity
        .badRequest()
        .body(error);
  }
  
  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<?> handleInsufficientStock(InsufficientStockException exception) {
    ApiError error = ApiError.builder()
        .code("INSUFFICIENT_STOCK")
        .message(exception.getMessage())
        .timestamp(LocalDateTime.now())
        .build();
        
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(error);
  }
  
  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MissingServletRequestParameterException.class,
    IllegalArgumentException.class
  })
  public ResponseEntity<?> handleBadRequest(Exception exception) {
    ApiError error = ApiError.builder()
        .code("BAD_REQUEST")
        .message(exception.getMessage())
        .timestamp(LocalDateTime.now())
        .build();
    
    return ResponseEntity
        .badRequest()
        .body(error);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDenied(AccessDeniedException exception) {
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .build();
  }
 
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGenericException(Exception exception) {
    ApiError error = ApiError.builder()
        .code("INTERNAL_SERVER_ERROR")
        .message(exception.getMessage()) // For development stage only
        .timestamp(LocalDateTime.now())
        .build();
    
    return ResponseEntity
        .status(500)
        .body(error);
  }
  
}
