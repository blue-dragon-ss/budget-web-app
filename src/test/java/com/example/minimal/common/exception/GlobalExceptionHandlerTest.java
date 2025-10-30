package com.example.minimal.common.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import com.example.minimal.common.constants.LogFields;
import com.example.minimal.common.exception.GlobalExceptionHandler.ErrorBody;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.common.exception.error.ErrorMessage;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ValidationErrorCodeResolver validationErrorCodeResolver;

    @InjectMocks
    private GlobalExceptionHandler handler;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void handleBind_shouldReturnErrorBodyUsingRequestTraceId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(LogFields.TRACE_ID, "trace-from-request");
        MDC.put(LogFields.TRACE_ID, "trace-from-mdc");

        BindException bindException = new BindException(new Object(), "target");
        FieldError fieldError = new FieldError("target", "fieldName", "default-message");
        bindException.addError(fieldError);

        when(validationErrorCodeResolver.resolve(any(FieldError.class))).thenReturn(Optional.of("VAL-9999"));

        ErrorBody body = handler.handleBind(bindException, request);

        assertThat(body.timestamp()).isNotBlank();
        assertThat(body.traceId()).isEqualTo("trace-from-request");
        assertThat(body.errorCode()).isEqualTo("VAL-9999");
        assertThat(body.message()).isEqualTo("default-message");
        assertThat(body.field()).isEqualTo("fieldName");
        assertThat(body.details()).isEmpty();
    }

    @Test
    void handleConstraintViolation_shouldReturnErrorBodyUsingMdcTraceId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MDC.put(LogFields.TRACE_ID, "trace-from-mdc");

        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("violation-message");

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        ErrorBody body = handler.handleConstraintViolation(exception, request);

        assertThat(body.timestamp()).isNotBlank();
        assertThat(body.traceId()).isEqualTo("trace-from-mdc");
        assertThat(body.errorCode()).isEqualTo(ErrorCode.COM_VAL_DEAFALT_ERROR);
        assertThat(body.message()).isEqualTo("violation-message");
        assertThat(body.field()).isNull();
        assertThat(body.details()).isEmpty();
    }

    @Test
    void handleIllegalArgument_shouldReturnErrorBodyWithoutTraceId() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        IllegalArgumentException exception = new IllegalArgumentException("bad request");

        ErrorBody body = handler.handleIllegalArgument(exception, request);

        assertThat(body.timestamp()).isNotBlank();
        assertThat(body.traceId()).isEmpty();
        assertThat(body.errorCode()).isEqualTo(ErrorCode.COM_BAD_REQUEST_MESSAGE);
        assertThat(body.message()).isEqualTo("bad request");
        assertThat(body.field()).isNull();
        assertThat(body.details()).containsExactly(IllegalArgumentException.class.getSimpleName());
    }

    @Test
    void handleUnexpectedPersistence_shouldFallbackToMdcTraceIdWhenRequestTraceIdBlank() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(LogFields.TRACE_ID, " ");
        MDC.put(LogFields.TRACE_ID, "trace-from-mdc");

        UnexpectedPersistenceException exception =
                new UnexpectedPersistenceException("field", "message", "SYS-9999", new RuntimeException("cause"));

        ErrorBody body = handler.handleUnexpectedPersistence(exception, request);

        assertThat(body.timestamp()).isNotBlank();
        assertThat(body.traceId()).isEqualTo("trace-from-mdc");
        assertThat(body.errorCode()).isEqualTo("SYS-9999");
        assertThat(body.message()).isEqualTo(ErrorMessage.DEAFALT_INTERNAL_SERVER_ERROR_MESSAGE);
        assertThat(body.field()).isEqualTo("field");
        assertThat(body.details()).containsExactly(UnexpectedPersistenceException.class.getSimpleName());
    }
}
