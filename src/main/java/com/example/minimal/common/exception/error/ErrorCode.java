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
    
    // IDEMPOTENCY
    public static final String IDE_VAL_DEFFERENT_REQUEST = "IDEMP-0001";
    public static final String IDE_VAL_SAME_KEY_RUNNING = "IDEMP-0002";
    
    // COMMON (example)
    public static final String COM_VAL_DEAFALT_ERROR = "VAL-0000";
    public static final String COM_JSON_PARSE_ERROR = "VAL-0001";
    public static final String COM_BAD_REQUEST_MESSAGE = "VAL-0001";
    public static final String COM_SERVER_ERROR = "SYS-0001";
}
