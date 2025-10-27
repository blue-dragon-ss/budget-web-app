package com.example.minimal.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiHeaders {
    public static final String LOCATION          = "Location";
    public static final String REQUEST_ID        = "X-Request-Id";
    public static final String TRACE_ID          = "X-Trace-Id";
    public static final String IDEMPOTENCY_KEY   = "X-Idempotency-Key";
    public static final String CORRELATION_ID    = "X-Correlation-Id";
    public static final String RATE_LIMIT_REMAINING = "X-RateLimit-Remaining"; // (future)
    
    public static final String REFERRER_POLICY   = "Referrer-Policy";
    public static final String FRAME_OPTIONS     = "X-Frame-Options";
    public static final String CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
    public static final String CACHE_CONTROL     = "Cache-Control";
    public static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
    public static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
}
