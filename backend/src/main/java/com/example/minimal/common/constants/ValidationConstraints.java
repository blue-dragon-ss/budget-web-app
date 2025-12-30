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
	public static final int YEAR_MONTH_LENGTH = 6;
	public static final int TITLE_MAX = 200;
	public static final int PAYER_MAX = 100;
	public static final int PAYMENT_METHOD_MAX = 50;

	// Numeric ranges
	public static final BigDecimal MIN_BIGDECIMAL_VALUE = new BigDecimal(0);
	public static final BigDecimal MAX_BIGDECIMAL_VALUE = new BigDecimal(999_999_999L);

	// IDs
	public static final int ULID_LENGTH = 26;

	// Timeouts / retries
	public static final Duration HTTP_CLIENT_TIMEOUT = Duration.ofSeconds(5);
	public static final int MAX_RETRY = 3;
}
