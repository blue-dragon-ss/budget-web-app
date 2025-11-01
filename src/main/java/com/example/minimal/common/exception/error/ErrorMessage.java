package com.example.minimal.common.exception.error;

import com.example.minimal.common.constants.ValidationConstraints;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessage {
	public static final String DEAFALT_INVALID_MESSAGE = "不正な入力です。";
	public static final String JSON_PARSE_ERROR_MESSAGE = "リクエストボディの形式が不正です。";
	public static final String DEAFALT_BAD_REQUEST_MESSAGE = "不正なリクエストです。";
	public static final String DEAFALT_CONFLICT_MESSAGE = "一意制約違反です。";
	public static final String DEAFALT_INTERNAL_SERVER_ERROR_MESSAGE = "予期しないエラーが発生しました。時間を置いて再度お試しください。";

	// Validation
	public static final String VAL_CODE_NOT_BLANK = "会員コードは必須です。";
	public static final String VAL_CODE_SIZE = "会員コードは" + ValidationConstraints.CODE_MIN + "〜"
			+ ValidationConstraints.CODE_MAX + "文字で指定してください。";
	public static final String VAL_NAME_NOT_BLANK = "会員名は必須です。";
	public static final String VAL_NAME_SIZE = "会員名は" + ValidationConstraints.NAME_MIN + "〜"
			+ ValidationConstraints.NAME_MAX + "文字で指定してください。";
	public static final String VAL_EMAIL_SIZE = "メールアドレスは最大" + ValidationConstraints.EMAIL_MAX + "文字です。";
	public static final String VAL_EMAIL_PATTERN = "メールアドレスの形式が正しくありません。";
	public static final String VAL_NOTE_SIZE = "備考は最大" + ValidationConstraints.NOTE_MAX + "文字です。";

	// Member
	public static final String MBR_CONFLICT_CODE = "会員コードは既に使用されています。";

	// Idempotency
	public static final String IDE_DEAFALT_ERROR_MESSAGE = "Idempotency-Keyの処理中にエラーが発生しました。";
	public static final String IDE_DEFFERENT_REQUEST_MESSAGE = "同一Idempotency-Keyで異なる内容のリクエストが送信されました。";
	public static final String IDE_SAME_KEY_RUNNING_MESSAGE = "同一Idempotency-Keyの処理が進行中です。しばらくしてから再実行してください。";

	// Server
	public static final String COM_SERVER_ERROR_MESSAGE = "内部エラーが発生しました。";
}
