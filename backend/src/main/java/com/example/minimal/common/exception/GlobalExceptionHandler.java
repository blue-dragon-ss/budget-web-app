package com.example.minimal.common.exception;

import java.time.Instant;
import java.util.List;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.minimal.common.constants.LogFields;
import com.example.minimal.common.exception.error.BusinessException;
import com.example.minimal.common.exception.error.CryptoOperationException;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.common.exception.error.ErrorMessage;
import com.example.minimal.common.exception.error.UnexpectedIOException;
import com.example.minimal.common.exception.error.ValidationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
			List<String> details) {
	}

	private static String now() {
		return Instant.now().toString();
	}

	/** Step4 のフィルタで request 属性 & MDC に設定済みの traceId を引き出す */
	private static String traceId(HttpServletRequest req) {
		if (req == null) {
			return "";
		}
		Object fromAttr = req.getAttribute(LogFields.TRACE_ID);
		if (fromAttr instanceof String s && !s.isBlank())
			return s;
		// 念のためMDCもフォールバック
		String s = MDC.get(LogFields.TRACE_ID);
		return (s == null || s.isBlank()) ? "" : s;
	}

	/* -------------------- 400: Validation 系 -------------------- */

	/** JSONボディの Bean Validation（@Valid @RequestBody） */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorBody handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
		FieldError fe = ex == null ? null
				: ex.getBindingResult().getFieldErrors().isEmpty() ? null
						: ex.getBindingResult().getFieldErrors().get(0);
		String field = fe == null ? null : fe.getField();
		String message = fe == null ? ErrorMessage.DEAFALT_INVALID_MESSAGE : fe.getDefaultMessage(); // 例）「会員名は必須です（VAL-0201）」
		// 共通仮コード：VAL-0000（各APIでFFRRに差し替え予定）
		String errorCode = validationErrorCodeResolver.resolve(fe).orElse(ErrorCode.COM_VAL_DEAFALT_ERROR);
		List<String> details = ex != null ? List.of(ex.getClass().getSimpleName()) : List.of();
		if (ex == null) {
			log.warn("MethodArgumentNotValid exception: path={}, traceId={}", req.getRequestURI(), traceId(req));
		} else {
			log.warn("MethodArgumentNotValid exception: path={}, traceId={}", req.getRequestURI(), traceId(req), ex);
		}
		return new ErrorBody(now(), traceId(req), errorCode, message, field, details);
	}

	/** クエリ/パスなどのバインドエラー（@Valid な QueryParam など） */
	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorBody handleBind(BindException ex, HttpServletRequest req) {
		FieldError fe = ex == null ? null
				: ex.getBindingResult().getFieldErrors().isEmpty() ? null
						: ex.getBindingResult().getFieldErrors().get(0);
		String field = fe == null ? null : fe.getField();
		String message = fe == null ? ErrorMessage.DEAFALT_INVALID_MESSAGE : fe.getDefaultMessage();
		String errorCode = validationErrorCodeResolver.resolve(fe).orElse(ErrorCode.COM_VAL_DEAFALT_ERROR);
		List<String> details = ex != null ? List.of(ex.getClass().getSimpleName()) : List.of();
		if (ex == null) {
			log.warn("Bind exception: path={}, traceId={}", req.getRequestURI(), traceId(req));
		} else {
			log.warn("Bind exception: path={}, traceId={}", req.getRequestURI(), traceId(req), ex);
		}
		return new ErrorBody(now(), traceId(req), errorCode, message, field, details);
	}

	/** メソッドレベル @Validated の ConstraintViolation（Controllerの引数検証など） */
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorBody handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
		String message = ErrorMessage.DEAFALT_INVALID_MESSAGE;
		List<String> details = List.of();
		if (ex != null) {
			if (ex.getConstraintViolations() != null) {
				message = ex.getConstraintViolations().stream().findFirst().map(v -> v.getMessage())
						.orElse(ErrorMessage.DEAFALT_INVALID_MESSAGE);
				details = List.of(ex.getClass().getSimpleName());
			}
			log.warn("ConstraintViolation exception: path={}, traceId={}", req.getRequestURI(), traceId(req), ex);
		} else {
			log.warn("ConstraintViolation exception: path={}, traceId={}", req.getRequestURI(), traceId(req));
		}
		return new ErrorBody(now(), traceId(req), ErrorCode.COM_VAL_DEAFALT_ERROR, message, null, details);
	}

	/** JSON 形式不正（パース不能） */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorBody handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
		if (ex == null) {
			log.warn("HttpMessageNotReadable exception: path={}, traceId={}", req.getRequestURI(), traceId(req));
		} else {
			log.warn("HttpMessageNotReadable exception: path={}, traceId={}", req.getRequestURI(), traceId(req), ex);
		}
		return new ErrorBody(now(), traceId(req), ErrorCode.COM_JSON_PARSE_ERROR, ErrorMessage.JSON_PARSE_ERROR_MESSAGE,
				null, ex != null ? List.of(ex.getClass().getSimpleName()) : List.of());
	}

	/** 不正引数例外 → 400 Bad Request */
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorBody handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
		if (ex == null) {
			log.warn("IllegalArgument exception: path={}, traceId={}", req.getRequestURI(), traceId(req));
			return new ErrorBody(now(), traceId(req), ErrorCode.COM_BAD_REQUEST_ERROR,
					ErrorMessage.DEAFALT_BAD_REQUEST_MESSAGE, null, List.of());
		}
		log.warn("IllegalArgument exception: path={}, traceId={}", req.getRequestURI(), traceId(req), ex);
		String message = ex.getMessage();
		if (message == null || message.isEmpty()) {
			message = ErrorMessage.DEAFALT_BAD_REQUEST_MESSAGE;
		}
		return new ErrorBody(now(), traceId(req), ErrorCode.COM_BAD_REQUEST_ERROR, message, null,
				List.of(ex.getClass().getSimpleName()));
	}

	/** 一意制約違反例外 → 400 Bad Request */
	@ExceptionHandler(DuplicateValueException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST) // 仕様次第では 409(CONFLICT) にしてもOK
	public ErrorBody handleDuplicate(DuplicateValueException ex, HttpServletRequest req) {
		if (ex == null) {
			log.warn("DuplicateValue exception: path={}, traceId={}", req.getRequestURI(), traceId(req));
			return new ErrorBody(now(), traceId(req), ErrorCode.COM_VAL_DEAFALT_ERROR,
					ErrorMessage.DEAFALT_INVALID_MESSAGE, null, List.of());
		}
		log.warn("DuplicateValue exception: path={}, traceId={}", req.getRequestURI(), traceId(req), ex);
		return new ErrorBody(now(), traceId(req), ex.getErrorCode(), // 例: "VAL-0105"
				ex.getMessage(), // メッセージ
				ex.getField(), // エラー対象フィールド名（例: "code"）
				List.of(ex.getClass().getSimpleName()));
	}

	/** 独自のバリデーション例外 */
	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorBody handleValidation(ValidationException ex, HttpServletRequest req) {
		if (ex == null) {
			log.warn("Validation exception: path={}, traceId={}", req.getRequestURI(), traceId(req));
			return new ErrorBody(now(), traceId(req), ErrorCode.COM_BAD_REQUEST_ERROR,
					ErrorMessage.DEAFALT_BAD_REQUEST_MESSAGE, null, List.of());
		}
		log.warn("Validation exception: path={}, traceId={}", req.getRequestURI(), traceId(req), ex);
		String field = ex.getField();
		String message = ex.getMessage(); // 例）「会員名は必須です（VAL-0201）」
		String errorCode = ex.getErrorCode();
		List<String> details = ex != null ? List.of(ex.getClass().getSimpleName()) : List.of();
		return new ErrorBody(now(), traceId(req), errorCode, message, field, details);
	}

	/* -------------------- 409: 一意制約など -------------------- */

	/** 冪等性競合例外 → 409 Conflict */
	@ExceptionHandler(IdempotencyConflictException.class)
	@ResponseStatus(HttpStatus.CONFLICT) // 冪等性競合のため 409 が適切
	public ErrorBody handleIdempotencyConflict(IdempotencyConflictException ex, HttpServletRequest req) {
		if (ex == null) {
			log.warn("IdempotencyConflict exception: path={}, traceId={}", req.getRequestURI(), traceId(req));
			return new ErrorBody(now(), traceId(req), ErrorCode.IDE_VAL_DEAFALT_ERROR,
					ErrorMessage.IDE_DEAFALT_ERROR_MESSAGE, null, List.of());
		}
		log.warn("IdempotencyConflict exception: path={}, traceId={}", req.getRequestURI(), traceId(req), ex);
		return new ErrorBody(now(), traceId(req), ex.getErrorCode(), // 例: "IDEMP-0001"
				ex.getMessage(), // エラーメッセージ
				ex.getField(), // エラー対象（例: "X-Idempotency-Key"）
				List.of(ex.getClass().getSimpleName()));
	}

	/** ビジネス例外 → 409 Conflict */
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorBody handleBusiness(BusinessException ex, HttpServletRequest req) {
		if (ex == null) {
			log.warn("Business exception: path={}, traceId={}", req.getRequestURI(), traceId(req));
			return new ErrorBody(now(), traceId(req), ErrorCode.COM_BAD_REQUEST_ERROR,
					ErrorMessage.DEAFALT_BAD_REQUEST_MESSAGE, null, List.of());
		}
		log.warn("Business exception: path={}, traceId={}", req.getRequestURI(), traceId(req), ex);
		String field = ex.getField();
		String message = ex.getMessage();
		String errorCode = ex.getErrorCode();
		List<String> details = ex != null ? List.of(ex.getClass().getSimpleName()) : List.of();
		return new ErrorBody(now(), traceId(req), errorCode, message, field, details);
	}

	/* -------------------- 500: その他予期しない例外 -------------------- */

	// 永続化関連の予期しない例外
	@ExceptionHandler(UnexpectedPersistenceException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorBody handleUnexpectedPersistence(UnexpectedPersistenceException ex, HttpServletRequest req) {
		if (ex == null) {
			log.error("UnhandledPersistence exception: path={}, traceId={}", req.getRequestURI(), traceId(req));
			return new ErrorBody(now(), traceId(req), ErrorCode.COM_SERVER_ERROR,
					ErrorMessage.DEAFALT_INTERNAL_SERVER_ERROR_MESSAGE, null, List.of());
		}
		log.error("UnhandledPersistence exception: path={}, traceId={}", req.getRequestURI(), traceId(req), ex);
		return new ErrorBody(now(), traceId(req), ex.getErrorCode(), // "SYS-0001"
				ex.getMessage(), ex.getField(), // 通常は null
				List.of(ex.getClass().getSimpleName()));
	}

	// IO 関連の予期しない例外
	@ExceptionHandler(UnexpectedIOException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorBody handleUnexpectedIO(UnexpectedIOException ex, HttpServletRequest req) {
		if (ex == null) {
			log.error("UnhandledIO exception: path={}, traceId={}", req.getRequestURI(), traceId(req));
			return new ErrorBody(now(), traceId(req), ErrorCode.COM_SERVER_ERROR,
					ErrorMessage.DEAFALT_INTERNAL_SERVER_ERROR_MESSAGE, null, List.of());
		}
		log.error("UnhandledIO exception: path={}, traceId={}", req.getRequestURI(), traceId(req), ex);
		return new ErrorBody(now(), traceId(req), ex.getErrorCode(), // "SYS-0001"
				ex.getMessage(), ex.getField(), List.of(ex.getClass().getSimpleName()));
	}

	// 暗号化/復号化関連の例外
	@ExceptionHandler(CryptoOperationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorBody handleCrypto(CryptoOperationException ex, HttpServletRequest req) {
		if (ex == null) {
			log.error("CryptoOperation exception: path={}, traceId={}", req.getRequestURI(), traceId(req));
			return new ErrorBody(now(), traceId(req), ErrorCode.COM_SERVER_ERROR,
					ErrorMessage.DEAFALT_INTERNAL_SERVER_ERROR_MESSAGE, null, List.of());
		}
		log.error("CryptoOperation exception: path={}, traceId={}", req.getRequestURI(), traceId(req), ex);
		return new ErrorBody(now(), traceId(req), ex.getErrorCode(), // 例: COM_SHA256_ALGORITHM_NOT_FOUND
				ex.getMessage(), ex.getField(), // 通常 null
				List.of(ex.getClass().getSimpleName()));
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorBody handleUnknown(Exception ex, HttpServletRequest req) {
		log.error("Unhandled exception: path={}, traceId={}", req.getRequestURI(), traceId(req), ex);
		return new ErrorBody(now(), traceId(req), ErrorCode.COM_SERVER_ERROR,
				ErrorMessage.DEAFALT_INTERNAL_SERVER_ERROR_MESSAGE, null,
				ex != null ? List.of(ex.getClass().getSimpleName()) : List.of());
	}
}
