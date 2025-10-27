package com.example.minimal.common.constants;

import java.time.Duration;

public final class ValidationConstraints {
    private ValidationConstraints() {}

    // String lengths
    public static final int CODE_MIN  = 1;
    public static final int CODE_MAX  = 50;
    public static final int NAME_MIN  = 1;
    public static final int NAME_MAX  = 100;
    public static final int EMAIL_MAX = 255;
    public static final int NOTE_MAX = 100;

    // IDs
    public static final int ULID_LENGTH = 26;

    // Timeouts / retries
    public static final Duration HTTP_CLIENT_TIMEOUT = Duration.ofSeconds(5);
    public static final int MAX_RETRY = 3;
}
