package com.example.minimal.common.exception.error;

import com.example.minimal.common.constants.ValidationConstraints;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessage {
	// Common
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
	public static final String VAL_YEAR_MONTH_SIZE = "年月は" + ValidationConstraints.YEAR_MONTH_LENGTH + "文字で指定してください。";
	public static final String VAL_YEAR_MONTH_NOT_BLANK = "年月は必須です。";
	public static final String VAL_YEAR_MONTH_PATTERN = "年月の形式が正しくありません。YYYY-MMの形式で指定してください。";
	public static final String VAL_ITEM_FILE_NOT_BLANK = "明細CSVは必須です。";
	public static final String VAL_ITEM_FILE_PATTERN = "明細CSVはCSV形式のファイルで指定してください。";
	public static final String VAL_ITEM_FILE_MAX_SIZE = "明細CSVのサイズが大きすぎます。";
	public static final String VAL_ITEM_FILE_CSV_NULL_BYTE = "明細CSVに不正なバイナリデータが含まれています。";

	// Member
	public static final String MBR_CONFLICT_CODE = "会員コードは既に使用されています。";

	// Idempotency
	public static final String IDE_DEAFALT_ERROR_MESSAGE = "Idempotency-Keyの処理中にエラーが発生しました。";
	public static final String IDE_DEFFERENT_REQUEST_MESSAGE = "同一Idempotency-Keyで異なる内容のリクエストが送信されました。";
	public static final String IDE_SAME_KEY_RUNNING_MESSAGE = "同一Idempotency-Keyの処理が進行中です。しばらくしてから再実行してください。";

	// Item
	public static final String ITM_CONFLICT_PUBLIC_ID = "公開IDは既に使用されています。";
	public static final String ITM_BAD_FOREIGN_KEY = "会員IDまたはカテゴリIDが存在しません。";

	// Item CSV Business
	public static final String ITM_CSV_HEADER = "明細CSVのヘッダーが正しくありません。";
	public static final String ITM_CSV_NO_DATA = "明細CSVにデータが存在しません。";
	public static final String ITM_CSV_SECOND_LINE_INVALID_DATA = "明細CSVの2行目に為替データが入っている可能性があります。";
	public static final String ITM_CSV_EXCHANGE_EMPTY_OR_OVER = "明細CSVの為替情報が無いか、" + ValidationConstraints.TITLE_MAX
			+ "文字を超えています。";

	// Item CSV Validation
	public static final String ITM_CSV_ERROR_USAGE_DATE = "利用日付はYYYY/MM/DDの形式で指定してください。";
	public static final String ITM_CSV_ERROR_TITLE = "タイトルは" + ValidationConstraints.TITLE_MAX + "文字以内で必ず指定してください。";
	public static final String ITM_CSV_ERROR_PAYER = "支払者は" + ValidationConstraints.PAYER_MAX + "文字以内で必ず指定してください。";
	public static final String ITM_CSV_ERROR_PAYMENT_METHOD = "支払方法は" + ValidationConstraints.PAYMENT_METHOD_MAX
			+ "文字以内で必ず指定してください。";
	public static final String ITM_CSV_ERROR_UDAGE_AMOUNT = "利用金額は" + ValidationConstraints.MIN_BIGDECIMAL_VALUE + "～"
			+ ValidationConstraints.MAX_BIGDECIMAL_VALUE + "の整数で指定してください。";
	public static final String ITM_CSV_ERROR_FEE_AMOUNT = "支払手数料は" + ValidationConstraints.MIN_BIGDECIMAL_VALUE + "～"
			+ ValidationConstraints.MAX_BIGDECIMAL_VALUE + "の整数で指定してください。";
	public static final String ITM_CSV_ERROR_TOTAL_AMOUNT = "合計金額は" + ValidationConstraints.MIN_BIGDECIMAL_VALUE + "～"
			+ ValidationConstraints.MAX_BIGDECIMAL_VALUE + "の整数で指定してください。";
	public static final String ITM_CSV_ERROR_CURRENT_MONTH_PAID = "当月支払額は" + ValidationConstraints.MIN_BIGDECIMAL_VALUE
			+ "～" + ValidationConstraints.MAX_BIGDECIMAL_VALUE + "の整数で指定してください。";
	public static final String ITM_CSV_ERROR_NEXT_MONTH_PAID = "翌月支払額は" + ValidationConstraints.MIN_BIGDECIMAL_VALUE
			+ "～" + ValidationConstraints.MAX_BIGDECIMAL_VALUE + "の整数で指定してください。";

	// sha256
	public static final String SHA256_ALGORITHM_NOT_FOUND = "SHA-256のアルゴリズムが見つかりません。";

	// Server
	public static final String COM_SERVER_ERROR_MESSAGE = "内部エラーが発生しました。";
	public static final String COM_SERVER_ERROR_IO_MESSAGE = "予期しないI/Oエラーが発生しました。";
}
