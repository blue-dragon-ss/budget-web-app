package com.example.minimal.common;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1) // ← 他のFilterより先に実行
public class CommonHeadersFilter extends OncePerRequestFilter {

    public static final String TRACE_ID = "traceId";
    public static final String IDEMPOTENCY_KEY = "idempotencyKey";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // ① Trace ID 決定（来ていれば引継ぎ／なければ生成）
        String traceId = Optional.ofNullable(req.getHeader("X-Trace-Id"))
                .filter(s -> !s.isBlank())
                .orElse(UUID.randomUUID().toString());

        // ② Idempotency-Key（任意）取得
        String idmpKey = Optional.ofNullable(req.getHeader("X-Idempotency-Key"))
                .filter(s -> !s.isBlank())
                .orElse(null);

        // ③ リクエスト属性・レスポンス・MDCへ設定
        req.setAttribute(TRACE_ID, traceId);
        res.setHeader("X-Trace-Id", traceId);
        MDC.put(TRACE_ID, traceId);

        if (idmpKey != null) {
            req.setAttribute(IDEMPOTENCY_KEY, idmpKey);
            MDC.put(IDEMPOTENCY_KEY, idmpKey);
        }

        // ④ セキュリティ系ヘッダ（共通）
        res.setHeader("Referrer-Policy", "no-referrer");
        res.setHeader("X-Frame-Options", "DENY");
        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("Cache-Control", "no-store");
     // res.setHeader("Content-Security-Policy", "default-src 'self'"); // ← 必要に応じて調整

        // ⑤ HSTS は HTTPS 通信時のみ
        if (req.isSecure()) {
            res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }

        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove(TRACE_ID);
            MDC.remove(IDEMPOTENCY_KEY);
        }
    }
}
