package com.example.minimal.common.exception;

import java.time.Instant;
import java.util.List;

import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
    Object fromAttr = req.getAttribute("traceId");
    if (fromAttr instanceof String s && !s.isBlank()) return s;
    // 念のためMDCもフォールバック
    String s = MDC.get("traceId");
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
    String message = fe == null ? "不正な入力です。" : fe.getDefaultMessage(); // 例）「会員名は必須です（VAL-0201）」
    // 共通仮コード：VAL-0000（各APIでFFRRに差し替え予定）
    return new ErrorBody(now(), traceId(req), "VAL-0000", message, field, List.of());
  }

  /** クエリ/パスなどのバインドエラー（@Valid な QueryParam など） */
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorBody handleBind(BindException ex, HttpServletRequest req) {
    FieldError fe = ex.getBindingResult().getFieldErrors().isEmpty()
        ? null : ex.getBindingResult().getFieldErrors().get(0);
    String field = fe == null ? null : fe.getField();
    String message = fe == null ? "不正な入力です。" : fe.getDefaultMessage();
    return new ErrorBody(now(), traceId(req), "VAL-0000", message, field, List.of());
  }

  /** メソッドレベル @Validated の ConstraintViolation（Controllerの引数検証など） */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorBody handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
    String message = ex.getConstraintViolations().stream()
        .findFirst().map(v -> v.getMessage()).orElse("不正な入力です。");
    return new ErrorBody(now(), traceId(req), "VAL-0000", message, null, List.of());
  }

  /** JSON 形式不正（パース不能） */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorBody handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
    return new ErrorBody(now(), traceId(req), "VAL-0001", "リクエストボディの形式が不正です。", null,
        List.of(ex.getClass().getSimpleName()));
  }
  
  /** 不正引数例外 → 400 Bad Request */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorBody handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
    return new ErrorBody(now(), traceId(req), "VAL-0002",
        ex.getMessage() != null ? ex.getMessage() : "不正なリクエストです。",
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
        java.util.List.of(ex.getClass().getSimpleName()));
  }

  /* -------------------- 409: 一意制約など -------------------- */

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorBody handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
    // 仕様のサンプルコード：VAL-0105（Unique違反）
    return new ErrorBody(now(), traceId(req), "VAL-0105", "一意制約違反です。", "code",
        List.of(ex.getClass().getSimpleName()));
  }

  /* -------------------- 500: その他予期しない例外 -------------------- */

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorBody handleUnknown(Exception ex, HttpServletRequest req) {
    return new ErrorBody(now(), traceId(req), "SYS-0001", "サーバ内部エラーが発生しました。", null,
        List.of(ex.getClass().getSimpleName()));
  }
}
