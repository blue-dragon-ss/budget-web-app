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
	public static final String ITM_VAL_SIZE_TOTAL_NUM = "VAL-20403";
	public static final String ITM_VAL_REQUIRED_UPDATE_ITEM_LIST = "VAL-20501";
	public static final String ITM_VAL_SIZE_UPDATE_ITEM_LIST = "VAL-20502";
	public static final String ITM_VAL_REQUIRED_UPDATE_ITEM = "VAL-20601";
	public static final String ITM_VAL_REQUIRED_ITEM_ID = "VAL-20701";
	public static final String ITM_VAL_SIZE_ITEM_ID = "VAL-20702";
	public static final String ITM_VAL_SIZE_DATE = "VAL-20802";
	public static final String ITM_VAL_RANGE_DATE = "VAL-20803";
	public static final String ITM_VAL_PATTERN_DATE = "VAL-20804";
	public static final String ITM_VAL_SIZE_TITLE = "VAL-20902";
	public static final String ITM_VAL_PATTERN_TITLE = "VAL-20904";
	public static final String ITM_VAL_SIZE_PAYER = "VAL-21002";
	public static final String ITM_VAL_PATTERN_PAYER = "VAL-21004";
	public static final String ITM_VAL_SIZE_PAYMENT_METHOD = "VAL-21102";
	public static final String ITM_VAL_PATTERN_PAYMENT_METHOD = "VAL-21104";
	public static final String ITM_VAL_RANGE_USAGE_AMOUNT = "VAL-21203";
	public static final String ITM_VAL_RANGE_FEE_AMOUNT = "VAL-21303";
	public static final String ITM_VAL_RANGE_TOTAL_AMOUNT = "VAL-21403";
	public static final String ITM_VAL_RANGE_CURRENT_MONTH_PAID = "VAL-21503";
	public static final String ITM_VAL_RANGE_NEXT_MONTH_PAID = "VAL-21603";
	public static final String ITM_VAL_RANGE_CATEGORY_ID = "VAL-21803";
	public static final String ITM_VAL_SIZE_MEMO = "VAL-21902";
	public static final String ITM_VAL_REQUIRED_ITEM_FILE = "VAL-22001";
	public static final String ITM_VAL_PATTERN_ITEM_FILE = "VAL-22004";
	public static final String ITM_VAL_MAX_SIZE_ITEM_FILE = "VAL-22006";
	public static final String ITM_VAL_NULL_BYTE_ITEM_FILE = "VAL-22007";
	public static final String ITM_VAL_MAX_SIZE_MEMO = "VAL-21902";

	// ITEM: BUSINESS CONFLICT
	public static final String ITM_BUS_UPDATE_LIST_SIZE_NOT_MATCH = "BUS-20201";
	public static final String ITM_BUS_UPDATE_COLUMN_NOT_EXIST = "BUS-20202";
	public static final String ITM_BUS_ALL_AMOUNT_NOT_EXIST = "BUS-20203";
	public static final String ITM_BUS_CATEGORY_ID_NOT_EXIST = "BUS-20204";
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
