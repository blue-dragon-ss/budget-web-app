package com.example.minimal.common.constants;

public final class Regexes {
	private Regexes() {
	}

	public static final String EMAIL = "^(|[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
	public static final String ULID = "^[0-9A-HJKMNP-TV-Z]{26}$";
	public static final String YEAR_MONTH = "^(19|20)\\d{2}-(0[1-9]|1[0-2])$";
	public static final String CSV_FILE_END = ".csv";
}
