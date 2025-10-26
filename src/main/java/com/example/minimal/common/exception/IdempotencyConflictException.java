package com.example.minimal.common.exception;

import lombok.Getter;

/**
 * Idempotency（冪等性）キーの競合を表す例外です。
 * 
 * <p>同一の X-Idempotency-Key に対して異なる内容のリクエストが送信された場合や、
 * 同一キーで処理中の要求が同時に到達した場合などにスローされます。</p>
 *
 * <p>この例外は業務データの重複（DuplicateValueException）とは異なり、
 * 冪等性制御のロジック上の競合を通知します。</p>
 */
@Getter
public class IdempotencyConflictException extends RuntimeException {

    private final String field;
    private final String errorCode;

    /**
     * コンストラクタ。
     *
     * @param field     エラーが発生したキー（例: "X-Idempotency-Key"）
     * @param message   エラーメッセージ
     * @param errorCode APIレスポンスで使用するエラーコード
     */
    public IdempotencyConflictException(String field, String message, String errorCode) {
        super(message);
        this.field = field;
        this.errorCode = errorCode;
    }
}
