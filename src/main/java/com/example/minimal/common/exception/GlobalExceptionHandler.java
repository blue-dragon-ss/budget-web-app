package com.example.minimal.common.exception;

import java.time.Instant;
import java.util.List;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.example.minimal.common.constants.LogFields;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.common.exception.error.ErrorMessage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private final ValidationErrorCodeResolver validationErrorCodeResolver;

  public GlobalExceptionHandler(ValidationErrorCodeResolver validationErrorCodeResolver) {
    this.validationErrorCodeResolver = validationErrorCodeResolver;
  }

  /** 仕様：timestamp/traceId/errorCode/message/field/details の固定形 */
  public record ErrorBody(
      String timestamp,
      String traceId,
      String errorCode,
      String message,
      String field,
      List<String> details
  ) {}

  private static String now() {
    return Instant.now().toString();
  }

  /** Step4 のフィルタで request 属性 & MDC に設定済みの traceId を引き出す */
  private static String traceId(HttpServletRequest req) {
    Object fromAttr = req.getAttribute(LogFields.TRACE_ID);
    if (fromAttr instanceof String s && !s.isBlank()) return s;
    // 念のためMDCもフォールバック
    String s = MDC.get(LogFields.TRACE_ID);
    return (s == null || s.isBlank()) ? "" : s;
  }

  /* -------------------- 400: Validation 系 -------------------- */

  /** JSONボディの Bean Validation（@Valid @RequestBody） */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorBody handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
    FieldError fe = ex.getBindingResult().getFieldErrors().isEmpty()
        ? null : ex.getBindingResult().getFieldErrors().get(0);
    String field = fe == null ? null : fe.getField();
    String message = fe == null ? ErrorMessage.DEAFALT_INVALID_MESSAGE : fe.getDefaultMessage(); // 例）「会員名は必須です（VAL-0201）」
    // 共通仮コード：VAL-0000（各APIでFFRRに差し替え予定）
    String errorCode = validationErrorCodeResolver.resolve(fe).orElse(ErrorCode.COM_VAL_DEAFALT_ERROR);
    return new ErrorBody(now(), traceId(req), errorCode, message, field, List.of());
  }

  /** クエリ/パスなどのバインドエラー（@Valid な QueryParam など） */
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorBody handleBind(BindException ex, HttpServletRequest req) {
    FieldError fe = ex.getBindingResult().getFieldErrors().isEmpty()
        ? null : ex.getBindingResult().getFieldErrors().get(0);
    String field = fe == null ? null : fe.getField();
    String message = fe == null ? ErrorMessage.DEAFALT_INVALID_MESSAGE : fe.getDefaultMessage();
    String errorCode = validationErrorCodeResolver.resolve(fe).orElse(ErrorCode.COM_VAL_DEAFALT_ERROR);
    return new ErrorBody(now(), traceId(req), errorCode, message, field, List.of());
  }

  /** メソッドレベル @Validated の ConstraintViolation（Controllerの引数検証など） */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorBody handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
    String message = ex.getConstraintViolations().stream()
        .findFirst().map(v -> v.getMessage()).orElse(ErrorMessage.DEAFALT_INVALID_MESSAGE);
    return new ErrorBody(now(), traceId(req), ErrorCode.COM_VAL_DEAFALT_ERROR, message, null, List.of());
  }

  /** JSON 形式不正（パース不能） */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorBody handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
    return new ErrorBody(now(), traceId(req), ErrorCode.COM_JSON_PARSE_ERROR, ErrorMessage.JSON_PARSE_ERROR_MESSAGE, null,
        List.of(ex.getClass().getSimpleName()));
  }
  
  /** 不正引数例外 → 400 Bad Request */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorBody handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
    return new ErrorBody(now(), traceId(req), ErrorCode.COM_BAD_REQUEST_MESSAGE,
        ex.getMessage() != null ? ex.getMessage() : ErrorMessage.DEAFALT_BAD_REQUEST_MESSAGE,
        null, List.of(ex.getClass().getSimpleName()));
  }
  
  @ExceptionHandler(DuplicateValueException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST) // 仕様次第では 409(CONFLICT) にしてもOK
  public ErrorBody handleDuplicate(DuplicateValueException ex, HttpServletRequest req) {
    return new ErrorBody(
        now(),
        traceId(req),
        ex.getErrorCode(),             // 例: "VAL-0105"
        ex.getMessage(),               // メッセージ
        ex.getField(),                 // エラー対象フィールド名（例: "code"）
        List.of(ex.getClass().getSimpleName()));
  }
  
  /* -------------------- 409: 一意制約など -------------------- */
  
  @ExceptionHandler(IdempotencyConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT) // 冪等性競合のため 409 が適切
  public ErrorBody handleIdempotencyConflict(IdempotencyConflictException ex, HttpServletRequest req) {
    return new ErrorBody(
        now(),
        traceId(req),
        ex.getErrorCode(),              // 例: "IDEMP-0001"
        ex.getMessage(),                // エラーメッセージ
        ex.getField(),                  // エラー対象（例: "X-Idempotency-Key"）
        List.of(ex.getClass().getSimpleName()));
  }

  /* -------------------- 500: その他予期しない例外 -------------------- */

  @ExceptionHandler(UnexpectedPersistenceException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorBody handleUnexpectedPersistence(UnexpectedPersistenceException ex, HttpServletRequest req) {
      return new ErrorBody(
          now(),
          traceId(req),
          ex.getErrorCode(),                                // "SYS-0001"
          ErrorMessage.DEAFALT_INTERNAL_SERVER_ERROR_MESSAGE,
          ex.getField(),                                     // 通常は null
          List.of(ex.getClass().getSimpleName())
      );
  }
  
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorBody handleUnknown(Exception ex, HttpServletRequest req) {
    return new ErrorBody(now(), traceId(req), ErrorCode.COM_SERVER_ERROR, ErrorMessage.DEAFALT_INTERNAL_SERVER_ERROR_MESSAGE, null,
        List.of(ex.getClass().getSimpleName()));
  }
}
