package com.example.minimal.common.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import com.example.minimal.common.constants.LogFields;
import com.example.minimal.common.exception.GlobalExceptionHandler.ErrorBody;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.common.exception.error.ErrorMessage;

class GlobalExceptionHandlerTest {

    private ValidationErrorCodeResolver resolver;
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        resolver = mock(ValidationErrorCodeResolver.class);
        handler = new GlobalExceptionHandler(resolver);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void handleMethodArgumentNotValidはフィールドエラー情報をレスポンスに含める() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "member");
        FieldError fieldError = new FieldError("member", "name", "名前が不正です");
        bindingResult.addError(fieldError);
        when(resolver.resolve(fieldError)).thenReturn(Optional.of("VAL-0201"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(LogFields.TRACE_ID, "trace-001");

        ErrorBody body = handler.handleMethodArgumentNotValid(
                new org.springframework.web.bind.MethodArgumentNotValidException(null, bindingResult),
                request);

        assertThat(body.timestamp()).isNotBlank();
        assertThat(body.traceId()).isEqualTo("trace-001");
        assertThat(body.errorCode()).isEqualTo("VAL-0201");
        assertThat(body.message()).isEqualTo("名前が不正です");
        assertThat(body.field()).isEqualTo("name");
        assertThat(body.details()).isEmpty();
    }

    @Test
    void handleHttpMessageNotReadableは固定のメッセージと詳細を返す() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(LogFields.TRACE_ID, "trace-002");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                "invalid", new RuntimeException("cause"), new MockHttpInputMessage(new byte[0]));

        ErrorBody body = handler.handleNotReadable(ex, request);

        assertThat(body.traceId()).isEqualTo("trace-002");
        assertThat(body.errorCode()).isEqualTo(ErrorCode.COM_JSON_PARSE_ERROR);
        assertThat(body.message()).isEqualTo(ErrorMessage.JSON_PARSE_ERROR_MESSAGE);
        assertThat(body.field()).isNull();
        assertThat(body.details()).containsExactly(HttpMessageNotReadableException.class.getSimpleName());
    }

    @Test
    void handleDuplicateは例外の情報をそのまま返す() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(LogFields.TRACE_ID, "trace-003");
        DuplicateValueException ex = new DuplicateValueException("code", "重複", "VAL-0105");

        ErrorBody body = handler.handleDuplicate(ex, request);

        assertThat(body.traceId()).isEqualTo("trace-003");
        assertThat(body.errorCode()).isEqualTo("VAL-0105");
        assertThat(body.message()).isEqualTo("重複");
        assertThat(body.field()).isEqualTo("code");
        assertThat(body.details()).containsExactly(DuplicateValueException.class.getSimpleName());
    }

    @Test
    void handleIdempotencyConflictはエラー詳細を保持する() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(LogFields.TRACE_ID, "trace-004");
        IdempotencyConflictException ex = new IdempotencyConflictException("X-Idempotency-Key", "競合", "IDEMP-0001");

        ErrorBody body = handler.handleIdempotencyConflict(ex, request);

        assertThat(body.traceId()).isEqualTo("trace-004");
        assertThat(body.errorCode()).isEqualTo("IDEMP-0001");
        assertThat(body.message()).isEqualTo("競合");
        assertThat(body.field()).isEqualTo("X-Idempotency-Key");
        assertThat(body.details()).containsExactly(IdempotencyConflictException.class.getSimpleName());
    }

    @Test
    void handleUnknownはリクエスト属性が無くてもMDCのtraceIdを利用する() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MDC.put(LogFields.TRACE_ID, "trace-mdc");

        ErrorBody body = handler.handleUnknown(new RuntimeException("boom"), request);

        assertThat(body.traceId()).isEqualTo("trace-mdc");
        assertThat(body.errorCode()).isEqualTo(ErrorCode.COM_SERVER_ERROR);
        assertThat(body.message()).isEqualTo(ErrorMessage.DEAFALT_INTERNAL_SERVER_ERROR_MESSAGE);
        assertThat(body.field()).isNull();
        assertThat(body.details()).containsExactly(RuntimeException.class.getSimpleName());
    }
}
