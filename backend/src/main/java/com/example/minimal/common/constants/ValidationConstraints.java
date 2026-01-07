package com.example.minimal.common.constants;

import java.math.BigDecimal;
import java.time.Duration;

public final class ValidationConstraints {
	private ValidationConstraints() {
	}

	// String lengths
	public static final int CODE_MIN = 1;
	public static final int CODE_MAX = 50;
	public static final int NAME_MIN = 1;
	public static final int NAME_MAX = 200;
	public static final int EMAIL_MAX = 320;
	public static final int NOTE_MAX = 10000;
	public static final int YEAR_MONTH_LENGTH = 7; // "YYYY-MM"
	public static final int TITLE_MAX = 200;
	public static final int PAYER_MAX = 100;
	public static final int PAYMENT_METHOD_MAX = 50;
	public static final int YEAR_MONTH_DATE_LENGTH = 10; // "yyyy/MM/dd"
	public static final int MEMO_MAX = 200;

	// Numeric ranges
	public static final int MIN_UPDATE_TOTAL_NUM = 1;
	public static final int MAX_UPDATE_TOTAL_NUM = 200;
	public static final int MIN_CATEGORY_ID = 0;
	public static final int MAX_CATEGORY_ID = 9999;
	public static final BigDecimal MIN_BIGDECIMAL_VALUE = new BigDecimal(0);
	public static final BigDecimal MAX_BIGDECIMAL_VALUE = new BigDecimal(999_999_999L);
	public static final String MIN_BIGDECIMAL_VALUE_STRING = "0";
	public static final int BIGDECIMAL_INTEGER = 9;
	public static final int BIGDECIMAL_FRACTION = 2;

	// List size
	public static final int MIN_UPDATE_ITEM_LIST_SIZE = 1;
	public static final int MAX_UPDATE_ITEM_LIST_SIZE = 200;

	// IDs
	public static final int ULID_LENGTH = 26;

	// Timeouts / retries
	public static final Duration HTTP_CLIENT_TIMEOUT = Duration.ofSeconds(5);
	public static final int MAX_RETRY = 3;
}
