package com.example.minimal.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.web.multipart.MultipartFile;

import com.example.minimal.common.constants.Regexes;
import com.example.minimal.common.constants.ValidationConstraints;
import com.example.minimal.common.exception.error.BusinessException;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.common.exception.error.ErrorMessage;
import com.example.minimal.common.exception.error.UnexpectedIOException;
import com.example.minimal.common.exception.error.ValidationException;
import com.example.minimal.item.dto.P203Request.Fields;

public class CsvReader {

	// 最大ファイルサイズ（バイト単位）
	private static final long MAX_BYTES = 10 * 1024 * 1024; // 10MB

	// CSVヘッダー定義
	private static class CsvHeader {
		private CsvHeader() {
		}

		public static final String USAGE_DATE = "利用日";
		public static final String TITLE = "利用店名・商品名";
		public static final String PAYER = "利用者";
		public static final String PAYMENT_METHOD = "支払方法";
		public static final String USAGE_AMOUNT = "利用金額";
		public static final String FEE_AMOUNT = "手数料/利息";
		public static final String TOTAL_AMOUNT = "支払総額";
		public static final String IS_NEW_ITEMS = "新規サイン";

		private static final String CURRENT_MONTH_PAID_TEMPLATE = "月支払金額";
		private static final String NEXT_MONTH_PAID_TEMPLATE = "月繰越残高";

		/** 現在月支払金額のヘッダー名を取得 */
		public static String getCurrentMonthPaidHeader(YearMonth ym) {
			int month = ym.getMonthValue();
			return month + CURRENT_MONTH_PAID_TEMPLATE;
		}

		/** 次月繰越残高のヘッダー名を取得 */
		public static String getNextMonthPaidHeader(YearMonth ym) {
			int month = ym.getMonthValue();
			int nextMonth = month == 12 ? 1 : month + 1;
			return nextMonth + NEXT_MONTH_PAID_TEMPLATE;
		}

		/** 必須ヘッダー一覧を取得 */
		public static List<String> getRequiredHeaders(YearMonth ym) {
			List<String> headers = new ArrayList<>();
			headers.add(USAGE_DATE);
			headers.add(TITLE);
			headers.add(PAYER);
			headers.add(PAYMENT_METHOD);
			headers.add(USAGE_AMOUNT);
			headers.add(FEE_AMOUNT);
			headers.add(TOTAL_AMOUNT);
			headers.add(getCurrentMonthPaidHeader(ym));
			headers.add(getNextMonthPaidHeader(ym));
			headers.add(IS_NEW_ITEMS);
			return headers;
		}
	}

	// 文字セット(UTF-8固定)
	private static Charset charset = StandardCharsets.UTF_8;

	/**
	 * 明細CSVのバリデーションチェック＋パース
	 * 
	 * @param file            明細CSVファイル
	 * @param targetYearMonth 対象年月
	 * @return パース結果
	 */
	public static CsvData validateAndParse(MultipartFile file, YearMonth targetYearMonth) {
		// 「明細CSV」のバリデーションチェック
		// --- A. メタチェック（fail fast 推奨） ---
		// 明細CSVファイル必須
		if (file == null || file.isEmpty()) {
			throw new ValidationException(Fields.itemFile, ErrorMessage.VAL_ITEM_FILE_NOT_BLANK,
					ErrorCode.ITM_VAL_REQUIRED_ITEM_FILE);
		}

		// 明細CSVファイルサイズ上限チェック
		if (file.getSize() > MAX_BYTES) {
			throw new ValidationException(Fields.itemFile, ErrorMessage.VAL_ITEM_FILE_MAX_SIZE,
					ErrorCode.ITM_VAL_MAX_SIZE_ITEM_FILE);
		}
		// 明細CSVファイル形式チェック（拡張子）
		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null || !originalFilename.toLowerCase().endsWith(Regexes.CSV_FILE_END)) {
			throw new ValidationException(Fields.itemFile, ErrorMessage.VAL_ITEM_FILE_PATTERN,
					ErrorCode.ITM_VAL_PATTERN_ITEM_FILE);
		}
		// --- B. ざっくりバイナリ検知（nullバイトが多い等を弾く） ---
		try (InputStream in = file.getInputStream();
				InputStream BOMStripped = BOMInputStream.builder().setInputStream(in).get();) {
			if (looksBinary(BOMStripped)) {
				// バイナリデータ検知
				throw new ValidationException(Fields.itemFile, ErrorMessage.VAL_ITEM_FILE_CSV_NULL_BYTE,
						ErrorCode.ITM_VAL_NULL_BYTE_ITEM_FILE);
			}
		} catch (IOException e) {
			// 入出力エラー
			throw new UnexpectedIOException(Fields.itemFile, ErrorMessage.COM_SERVER_ERROR_IO_MESSAGE,
					ErrorCode.COM_SERVER_ERROR, e);
		}

		// --- C. CSVパースと中身のバリデーションチェック ---
		List<CsvRow> rows = new ArrayList<>();
		List<CsvParseError> errors = new ArrayList<>();

		// CSVをBOM除去＋指定文字セットで読み込み
		try (InputStream raw = file.getInputStream();
				InputStream bomStripped = BOMInputStream.builder().setInputStream(raw).get();
				BufferedReader reader = new BufferedReader(new InputStreamReader(bomStripped, charset))) {
			CSVFormat format = CSVFormat.DEFAULT.builder().setHeader() // 1行目をヘッダーとして扱う
					.setSkipHeaderRecord(true) // データ行から処理
					.setTrim(true).setIgnoreEmptyLines(true).build();
			// CSVパース
			try (CSVParser parser = format.parse(reader)) {
				// ヘッダー存在チェック
				Map<String, Integer> headerMap = parser.getHeaderMap();
				for (String required : CsvHeader.getRequiredHeaders(targetYearMonth)) {
					if (!headerMap.containsKey(required)) {
						// 1行目のヘッダー内容チェック：BUS-20301
						throw new BusinessException(Fields.itemFile,
								ErrorMessage.ITM_CSV_HEADER.concat("CSV : " + headerMap.keySet().toString())
										.concat(" Required : " + CsvHeader.getRequiredHeaders(targetYearMonth)),
								ErrorCode.ITM_BUS_CSV_HEADER);
					}
				}

				List<CSVRecord> recordList = parser.getRecords();
				// データ行存在チェック
				if (recordList.isEmpty()) {
					// 2行目以降が存在するかチェック：BUS-20302
					throw new BusinessException(Fields.itemFile, ErrorMessage.ITM_CSV_NO_DATA,
							ErrorCode.ITM_BUS_CSV_NO_DATA);
				}

				// 「明細CSV」の中身をバリデーションチェックをしつつDB送信用にデータを整形する
				for (CSVRecord record : recordList) {
					long lineNo = record.getRecordNumber() + 1; // ヘッダー行を+1するイメージ（環境でズレる場合あり）
					// 取り出し（列名で）
					LocalDate ym = StringUtils.parseDateOrNull(record.get(CsvHeader.USAGE_DATE));
					String title = StringUtils.trim(record.get(CsvHeader.TITLE));

					// 次の行の「利用日」が空の場合、為替情報の処理へ
					if (ym == null) {
						if (rows.isEmpty()) {
							// 2行目のデータの「利用日」が空の場合：BUS-20303（為替データが2行目に入っている）
							throw new BusinessException(Fields.itemFile,
									ErrorMessage.ITM_CSV_SECOND_LINE_INVALID_DATA + record.toMap().toString(),
									ErrorCode.ITM_BUS_CSV_SECOND_LINE_INVALID_DATA);
						} else {
							// 為替情報行の「利用店名・商品名」のバリデーションチェック
							if (!StringUtils.isNullOrEmpty(title)
									&& StringUtils.isUnderMaxLength(title, ValidationConstraints.TITLE_MAX)) {
								// 問題なければ「利用店名・商品名」を現在行Entityの「メモ」に格納（$1）
								int lastIndex = rows.size() - 1;
								CsvRow lastRow = rows.get(lastIndex);
								rows.set(lastIndex,
										new CsvRow(lastRow.usageDate(), lastRow.title(), lastRow.payer(),
												lastRow.paymentMethod(), lastRow.usageAmount(), lastRow.feeAmount(),
												lastRow.totalAmount(), lastRow.currentMonthPaid(),
												lastRow.nextMonthPaid(), lastRow.isNewItems(), title));
							} else {
								// エラーの場合、レスポンスへエラー内容を次の行数として格納し、失敗件数を1増やして処理継続（#4）
								errors.add(new CsvParseError((int) lineNo, ErrorCode.ITM_VAL_CSV_ERROR_EXCHANGE,
										ErrorMessage.ITM_CSV_EXCHANGE_EMPTY_OR_OVER));
							}
							continue; // 次の行へ
						}
					}
					// 通常の明細行の取り出し・バリデーションチェック
					// 現在行について「1.2.1 明細CSV」に書かれているバリデーションをチェック
					// エラーの場合、レスポンスへエラー内容を現在の行として格納し、失敗件数を1増やして次の行へ（#4）

					// 利用日のバリデーションチェックは為替情報のエラーと統合しているので必要なし
					// 通常の利用店名・商品名のバリデーションチェック
					if (StringUtils.isNullOrEmpty(title)
							|| !StringUtils.isUnderMaxLength(title, ValidationConstraints.TITLE_MAX)) {
						errors.add(new CsvParseError((int) lineNo, ErrorCode.ITM_VAL_CSV_ERROR_TITLE,
								ErrorMessage.ITM_CSV_ERROR_TITLE));
						continue;
					}

					// 利用者のバリデーションチェック
					String payer = StringUtils.trim(record.get(CsvHeader.PAYER));
					if (StringUtils.isNullOrEmpty(payer)
							|| !StringUtils.isUnderMaxLength(payer, ValidationConstraints.PAYER_MAX)) {
						errors.add(new CsvParseError((int) lineNo, ErrorCode.ITM_VAL_CSV_ERROR_PAYER,
								ErrorMessage.ITM_CSV_ERROR_PAYER));
						continue;
					}

					// 支払方法のバリデーションチェック
					String paymentMethod = StringUtils.trim(record.get(CsvHeader.PAYMENT_METHOD));
					if (StringUtils.isNullOrEmpty(paymentMethod)
							|| !StringUtils.isUnderMaxLength(paymentMethod, ValidationConstraints.PAYMENT_METHOD_MAX)) {
						errors.add(new CsvParseError((int) lineNo, ErrorCode.ITM_VAL_CSV_ERROR_PAYMENT_METHOD,
								ErrorMessage.ITM_CSV_ERROR_PAYMENT_METHOD));
						continue;
					}

					// 利用金額のバリデーションチェック
					BigDecimal usageAmount = StringUtils.parseBigDecimalOrNull(record.get(CsvHeader.USAGE_AMOUNT));
					if (usageAmount == null) {
						errors.add(new CsvParseError((int) lineNo, ErrorCode.ITM_VAL_CSV_ERROR_UDAGE_AMOUNT,
								ErrorMessage.ITM_CSV_ERROR_UDAGE_AMOUNT));
						continue;
					}

					// 支払手数料のバリデーションチェック
					BigDecimal feeAmount = StringUtils.parseBigDecimalOrNull(record.get(CsvHeader.FEE_AMOUNT));
					if (feeAmount == null) {
						errors.add(new CsvParseError((int) lineNo, ErrorCode.ITM_VAL_CSV_ERROR_FEE_AMOUNT,
								ErrorMessage.ITM_CSV_ERROR_FEE_AMOUNT));
						continue;
					}

					// 支払総額のバリデーションチェック
					BigDecimal totalAmount = StringUtils.parseBigDecimalOrNull(record.get(CsvHeader.TOTAL_AMOUNT));
					if (totalAmount == null) {
						errors.add(new CsvParseError((int) lineNo, ErrorCode.ITM_VAL_CSV_ERROR_TOTAL_AMOUNT,
								ErrorMessage.ITM_CSV_ERROR_TOTAL_AMOUNT));
						continue;
					}

					// 当月支払金額のバリデーションチェック
					BigDecimal currentMonthPaid = StringUtils
							.parseBigDecimalOrNull(record.get(CsvHeader.getCurrentMonthPaidHeader(targetYearMonth)));
					if (currentMonthPaid == null) {
						errors.add(new CsvParseError((int) lineNo, ErrorCode.ITM_VAL_CSV_ERROR_CURRENT_MONTH_PAID,
								ErrorMessage.ITM_CSV_ERROR_CURRENT_MONTH_PAID));
						continue;
					}

					// 次月繰越残高のバリデーションチェック
					BigDecimal nextMonthPaid = StringUtils
							.parseBigDecimalOrNull(record.get(CsvHeader.getNextMonthPaidHeader(targetYearMonth)));
					if (nextMonthPaid == null) {
						errors.add(new CsvParseError((int) lineNo, ErrorCode.ITM_VAL_CSV_ERROR_NEXT_MONTH_PAID,
								ErrorMessage.ITM_CSV_ERROR_NEXT_MONTH_PAID));
						continue;
					}

					// 新規サインはチェックなしで取込用に変換
					boolean isNewItems = !StringUtils.isNullOrTrimmedEmpty(record.get(CsvHeader.IS_NEW_ITEMS));

					// 問題なければDBに送るEntityに「補足1．DBへのデータ取込」を通りに格納
					rows.add(new CsvRow(ym, title, payer, paymentMethod, usageAmount, feeAmount, totalAmount,
							currentMonthPaid, nextMonthPaid, isNewItems, null) /* メモはnull */);
				}
			} catch (IllegalArgumentException e) {
				// record.get("xxx") で列が存在しないなど
				throw new BusinessException(Fields.itemFile, ErrorMessage.ITM_CSV_HEADER.concat(e.getMessage()),
						ErrorCode.ITM_BUS_CSV_HEADER);
			}
		} catch (IOException e) {
			// 入出力エラー
			throw new UnexpectedIOException(Fields.itemFile, ErrorMessage.COM_SERVER_ERROR_IO_MESSAGE,
					ErrorCode.COM_SERVER_ERROR, e);
		}
		// 取込データ＋エラー内容を返却
		return new CsvData(rows, errors);
	}

	// ざっくりバイナリ検知（nullバイトが多い等を弾く）
	private static boolean looksBinary(InputStream i) throws IOException {
		InputStream in = i;
		if (!in.markSupported()) {
			// mark/reset非対応ストリームの場合、対応させる
			in = new BufferedInputStream(i);
		}
		in.mark(4096);
		byte[] buf = in.readNBytes(4096);
		in.reset();

		if (buf.length == 0)
			return false;

		int nullCount = 0;
		for (byte b : buf) {
			if (b == 0x00)
				nullCount++;
		}
		// 先頭4KBにnullがそこそこあるならバイナリ扱い（基準は調整）
		return nullCount > 0;
	}

	// CSV解析エラー情報
	public record CsvParseError(
			int lineNumber, // 行番号
			String code, // エラーコード
			String errorMessage) { // エラーメッセージ
	}

	// CSVデータ情報
	public record CsvRow(
			LocalDate usageDate, // 利用日
			String title, // 利用店名・商品名
			String payer, // 利用者
			String paymentMethod, // 支払方法
			BigDecimal usageAmount, // 利用金額
			BigDecimal feeAmount, // 支払手数料
			BigDecimal totalAmount, // 支払総額
			BigDecimal currentMonthPaid, // 12月支払金額
			BigDecimal nextMonthPaid, // 1月繰越残高
			boolean isNewItems, // 新規サイン
			String memo) { // メモ
	}

	// CSV全体情報
	public record CsvData(
			List<CsvRow> rows, // データ行
			List<CsvParseError> errors) { // エラー行
	}
}
