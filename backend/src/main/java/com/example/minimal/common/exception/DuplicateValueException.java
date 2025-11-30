package com.example.minimal.common.exception;

import lombok.Getter;

@Getter
public class DuplicateValueException extends RuntimeException {
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
  public DuplicateValueException(String field, String message, String errorCode) {
    super(message);
    this.field = field;
    this.errorCode = errorCode;
  }
}
