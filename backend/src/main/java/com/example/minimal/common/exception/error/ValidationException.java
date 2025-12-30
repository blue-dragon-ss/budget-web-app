package com.example.minimal.common.exception.error;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
	/**
	 * シリアルバージョンUID
	 */
	private static final long serialVersionUID = 1L;
	private final String field;
	private final String errorCode; // 例: VAL-0105

	/**
	 * コンストラクタ。
	 *
	 * @param field     エラーが発生したキー（例: "code"）
	 * @param message   エラーメッセージ
	 * @param errorCode APIレスポンスで使用するエラーコード
	 */
	public ValidationException(String field, String message, String errorCode) {
		super(message);
		this.field = field;
		this.errorCode = errorCode;
	}
}
