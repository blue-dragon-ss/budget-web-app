package com.example.minimal.common.exception.error;

import lombok.Getter;

@Getter
public class UnexpectedIOException extends RuntimeException {
	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 1L;
	private final String field; // 特定フィールドがなければ null
	private final String errorCode; // 例: "SYS-0001"

	/**
	 * コンストラクタ。
	 *
	 * @param field     エラーが発生したキー（例: "code"）
	 * @param message   エラーメッセージ
	 * @param errorCode APIレスポンスで使用するエラーコード
	 * @param cause     原因となった例外
	 */
	public UnexpectedIOException(String field, String message, String errorCode, Throwable cause) {
		super(message, cause);
		this.field = field;
		this.errorCode = errorCode;
	}
}
