package com.example.minimal.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;

class ValidationErrorCodeResolverTest {

    @Test
    void resolveShouldReturnEmptyAndKeepMappersEmptyWhenConstructedWithNull() throws Exception {
        ValidationErrorCodeResolver resolver = new ValidationErrorCodeResolver(null);

        Field mappersField = ValidationErrorCodeResolver.class.getDeclaredField("mappers");
        mappersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<FieldErrorCodeMapper> mappersInitial = (List<FieldErrorCodeMapper>) mappersField.get(resolver);
        assertThat(mappersInitial).isEmpty();

        Optional<String> nullResult = resolver.resolve(null);
        assertThat(nullResult).isEmpty();

        FieldError fieldError = new FieldError("object", "field", "default");
        Optional<String> fieldResult = resolver.resolve(fieldError);
        assertThat(fieldResult).isEmpty();

        @SuppressWarnings("unchecked")
        List<FieldErrorCodeMapper> mappersAfterResolve = (List<FieldErrorCodeMapper>) mappersField.get(resolver);
        assertThat(mappersAfterResolve).isEmpty();
    }
}
