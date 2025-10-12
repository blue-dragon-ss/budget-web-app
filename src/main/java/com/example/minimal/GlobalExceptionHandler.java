package com.example.minimal;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> body(HttpStatus status, String message, String path) {
        return Map.of(
            "timestamp", OffsetDateTime.now(ZoneOffset.UTC).toString(),
            "status", status.value(),
            "error", status.getReasonPhrase(),
            "message", message == null ? "" : message,
            "path", path,
            "requestId", MDC.get("rid")
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        var st = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(body(st, ex.getMessage(), req.getRequestURI()), st);
        }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleInternal(Exception ex, HttpServletRequest req) {
        var st = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(body(st, ex.getMessage(), req.getRequestURI()), st);
    }
}