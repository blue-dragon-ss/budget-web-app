package com.example.minimal.common.exception;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class ValidationErrorCodeResolver {

    private final List<FieldErrorCodeMapper> mappers;

    public ValidationErrorCodeResolver(List<FieldErrorCodeMapper> mappers) {
        this.mappers = mappers == null ? List.of() : List.copyOf(mappers);
    }

    public Optional<String> resolve(FieldError error) {
        if (error == null) {
            return Optional.empty();
        }
        return mappers.stream()
                .filter(mapper -> mapper.supports(error))
                .map(mapper -> mapper.resolve(error))
                .flatMap(Optional::stream)
                .findFirst();
    }
}
