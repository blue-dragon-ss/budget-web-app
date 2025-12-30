package com.example.minimal.common.exception.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorCode {
	// MEMBER: VALIDATION
	public static final String MBR_VAL_REQUIRED_CODE = "VAL-0101";
	public static final String MBR_VAL_SIZE_CODE = "VAL-0102";
	public static final String MBR_VAL_CONFLICT_CODE = "VAL-0105";
	public static final String MBR_VAL_REQUIRED_NAME = "VAL-0201";
	public static final String MBR_VAL_SIZE_NAME = "VAL-0202";
	public static final String MBR_VAL_SIZE_EMAIL = "VAL-0302";
	public static final String MBR_VAL_FORMAT_EMAIL = "VAL-0303";
	public static final String MBR_VAL_SIZE_NOTE = "VAL-0402";

	// ITEM: VALIDATION
	public static final String ITM_VAL_REQUIRED_YEAR_MONTH = "VAL-20101";
	public static final String ITM_VAL_SIZE_YEAR_MONTH = "VAL-20102";
	public static final String ITM_VAL_PATTERN_YEAR_MONTH = "VAL-20104";
	public static final String ITM_VAL_REQUIRED_ITEM_FILE = "VAL-22001";
	public static final String ITM_VAL_PATTERN_ITEM_FILE = "VAL-22004";
	public static final String ITM_VAL_MAX_SIZE_ITEM_FILE = "VAL-22006";
	public static final String ITM_VAL_NULL_BYTE_ITEM_FILE = "VAL-22007";
	public static final String ITM_VAL_MAX_SIZE_MEMO = "VAL-21902";

	// ITEM: BUSINESS CONFLICT
	public static final String ITM_BUS_CSV_HEADER = "BUS-20301";
	public static final String ITM_BUS_CSV_NO_DATA = "BUS-20302";
	public static final String ITM_BUS_CSV_SECOND_LINE_INVALID_DATA = "BUS-20303";
	public static final String ITM_BUS_CONFLICT_PUBLIC_ID = "BUS-20304";
	public static final String ITM_BUS_BAD_FOREIGN_KEY = "BUS-20305";

	// ITEM: CSV VALIDATION
	public static final String ITM_VAL_CSV_ERROR_USAGE_DATE = "VAL-20899";
	public static final String ITM_VAL_CSV_ERROR_TITLE = "VAL-20999";
	public static final String ITM_VAL_CSV_ERROR_PAYER = "VAL-21099";
	public static final String ITM_VAL_CSV_ERROR_PAYMENT_METHOD = "VAL-21199";
	public static final String ITM_VAL_CSV_ERROR_UDAGE_AMOUNT = "VAL-21299";
	public static final String ITM_VAL_CSV_ERROR_FEE_AMOUNT = "VAL-21399";
	public static final String ITM_VAL_CSV_ERROR_TOTAL_AMOUNT = "VAL-21499";
	public static final String ITM_VAL_CSV_ERROR_CURRENT_MONTH_PAID = "VAL-21599";
	public static final String ITM_VAL_CSV_ERROR_NEXT_MONTH_PAID = "VAL-21699";

	// IDEMPOTENCY
	public static final String IDE_VAL_DEAFALT_ERROR = "IDEMP-0000";
	public static final String IDE_VAL_DEFFERENT_REQUEST = "IDEMP-0001";
	public static final String IDE_VAL_SAME_KEY_RUNNING = "IDEMP-0002";

	// COMMON
	public static final String COM_VAL_DEAFALT_ERROR = "VAL-0000";
	public static final String COM_JSON_PARSE_ERROR = "VAL-0001";
	public static final String COM_BAD_REQUEST_ERROR = "VAL-0001";
	public static final String COM_SERVER_ERROR = "SYS-0001";
	public static final String COM_SHA256_ALGORITHM_NOT_FOUND = "SYS-0002";
}
