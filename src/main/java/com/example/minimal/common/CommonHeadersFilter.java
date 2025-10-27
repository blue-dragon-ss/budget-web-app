package com.example.minimal.common;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.minimal.common.constants.ApiHeaders;
import com.example.minimal.common.constants.LogFields;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1) // ← 他のFilterより先に実行
public class CommonHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // ① Trace ID 決定（来ていれば引継ぎ／なければ生成）
        String traceId = Optional.ofNullable(req.getHeader(ApiHeaders.TRACE_ID))
                .filter(s -> !s.isBlank())
                .orElse(UUID.randomUUID().toString());

        // ② Idempotency-Key（任意）取得
        String idmpKey = Optional.ofNullable(req.getHeader(ApiHeaders.IDEMPOTENCY_KEY))
                .filter(s -> !s.isBlank())
                .orElse(null);

        // ③ リクエスト属性・レスポンス・MDCへ設定
        req.setAttribute(LogFields.TRACE_ID, traceId);
        res.setHeader(ApiHeaders.TRACE_ID, traceId);
        MDC.put(LogFields.TRACE_ID, traceId);

        if (idmpKey != null) {
            req.setAttribute(LogFields.IDEMPOTENCY_KEY, idmpKey);
            MDC.put(LogFields.IDEMPOTENCY_KEY, idmpKey);
        }

        // ④ セキュリティ系ヘッダ（共通）
        res.setHeader(ApiHeaders.REFERRER_POLICY, "no-referrer");
        res.setHeader(ApiHeaders.FRAME_OPTIONS, "DENY");
        res.setHeader(ApiHeaders.CONTENT_TYPE_OPTIONS, "nosniff");
        res.setHeader(ApiHeaders.CACHE_CONTROL, "no-store");
//      res.setHeader(ApiHeaders.CONTENT_SECURITY_POLICY, "default-src 'self'"); // ← 必要に応じて調整

        // ⑤ HSTS は HTTPS 通信時のみ
        if (req.isSecure()) {
            res.setHeader(ApiHeaders.STRICT_TRANSPORT_SECURITY, "max-age=31536000; includeSubDomains");
        }

        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove(LogFields.TRACE_ID);
            MDC.remove(LogFields.IDEMPOTENCY_KEY);
        }
    }
}
