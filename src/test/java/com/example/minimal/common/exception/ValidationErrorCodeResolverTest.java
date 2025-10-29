package com.example.minimal.common.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;

class ValidationErrorCodeResolverTest {

    @Test
    void nullを渡した場合は空のOptionalを返す() {
        ValidationErrorCodeResolver resolver = new ValidationErrorCodeResolver(List.of());

        assertThat(resolver.resolve(null)).isEmpty();
    }

    @Test
    void 最初にマッチしたマッパーの結果を返す() {
        FieldErrorCodeMapper mapper1 = mock(FieldErrorCodeMapper.class);
        FieldErrorCodeMapper mapper2 = mock(FieldErrorCodeMapper.class);
        FieldError fieldError = new FieldError("target", "name", "invalid");

        when(mapper1.supports(fieldError)).thenReturn(true);
        when(mapper1.resolve(fieldError)).thenReturn(Optional.empty());
        when(mapper2.supports(fieldError)).thenReturn(true);
        when(mapper2.resolve(fieldError)).thenReturn(Optional.of("VAL-9999"));

        ValidationErrorCodeResolver resolver = new ValidationErrorCodeResolver(List.of(mapper1, mapper2));

        assertThat(resolver.resolve(fieldError)).contains("VAL-9999");
        verify(mapper1).supports(fieldError);
        verify(mapper1).resolve(fieldError);
        verify(mapper2).supports(fieldError);
        verify(mapper2).resolve(fieldError);
    }

    @Test
    void サポートしないマッパーはスキップされる() {
        FieldErrorCodeMapper mapper = mock(FieldErrorCodeMapper.class);
        FieldError fieldError = new FieldError("target", "field", "invalid");
        when(mapper.supports(fieldError)).thenReturn(false);

        ValidationErrorCodeResolver resolver = new ValidationErrorCodeResolver(List.of(mapper));

        assertThat(resolver.resolve(fieldError)).isEmpty();
        verify(mapper).supports(fieldError);
        verify(mapper, never()).resolve(fieldError);
    }
}
