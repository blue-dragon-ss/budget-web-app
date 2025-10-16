package com.example.minimal.exception;

import java.time.OffsetDateTime;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

// 既存に @RestControllerAdvice が付いていればそのまま利用
@RestControllerAdvice
public class GlobalExceptionHandler {

  private record ApiError(
      OffsetDateTime timestamp,
      String traceId,
      String code,
      String message
  ) {}

  private ResponseEntity<ApiError> build(HttpStatus status, String code, String message) {
    var body = new ApiError(
        OffsetDateTime.now(),
        MDC.get("traceId"),
        code,
        message
    );
    return ResponseEntity.status(status).body(body);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
    var msg = ex.getBindingResult().getAllErrors().stream()
        .findFirst().map(e -> e.getDefaultMessage()).orElse("Invalid request");
    return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", msg);
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ApiError> handleBind(BindException ex) {
    var msg = ex.getAllErrors().stream()
        .findFirst().map(e -> e.getDefaultMessage()).orElse("Invalid request");
    return build(HttpStatus.BAD_REQUEST, "BIND_ERROR", msg);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
    return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnknown(Exception ex) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected error");
  }
}
