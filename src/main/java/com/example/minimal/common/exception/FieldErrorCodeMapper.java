package com.example.minimal.common.exception;

import java.util.Optional;

import org.springframework.validation.FieldError;

public interface FieldErrorCodeMapper {
    boolean supports(FieldError error);

    Optional<String> resolve(FieldError error);
}
