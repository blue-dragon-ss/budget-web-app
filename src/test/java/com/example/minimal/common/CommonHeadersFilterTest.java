package com.example.minimal.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.example.minimal.common.constants.ApiHeaders;
import com.example.minimal.common.constants.LogFields;
import com.example.minimal.common.util.IdGenerator;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

class CommonHeadersFilterTest {

    private final CommonHeadersFilter filter = new CommonHeadersFilter();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void ヘッダが存在しない場合はトレースIDを生成して設定する() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        try (MockedStatic<IdGenerator> mocked = Mockito.mockStatic(IdGenerator.class)) {
            mocked.when(IdGenerator::newId).thenReturn("generated-trace-id");

            AtomicBoolean chainInvoked = new AtomicBoolean(false);
            FilterChain chain = (req, res) -> {
                chainInvoked.set(true);
                assertThat(req.getAttribute(LogFields.TRACE_ID)).isEqualTo("generated-trace-id");
                assertThat(MDC.get(LogFields.TRACE_ID)).isEqualTo("generated-trace-id");
                assertThat(req.getAttribute(LogFields.IDEMPOTENCY_KEY)).isNull();
                assertThat(MDC.get(LogFields.IDEMPOTENCY_KEY)).isNull();
            };

            filter.doFilter(request, response, chain);

            assertThat(chainInvoked).isTrue();
            assertThat(response.getHeader(ApiHeaders.TRACE_ID)).isEqualTo("generated-trace-id");
            assertThat(response.getHeader(ApiHeaders.REFERRER_POLICY)).isEqualTo("no-referrer");
            assertThat(response.getHeader(ApiHeaders.FRAME_OPTIONS)).isEqualTo("DENY");
            assertThat(response.getHeader(ApiHeaders.CONTENT_TYPE_OPTIONS)).isEqualTo("nosniff");
            assertThat(response.getHeader(ApiHeaders.CACHE_CONTROL)).isEqualTo("no-store");
            assertThat(response.getHeader(ApiHeaders.STRICT_TRANSPORT_SECURITY)).isNull();
            mocked.verify(IdGenerator::newId, Mockito.times(1));
        }

        assertThat(MDC.get(LogFields.TRACE_ID)).isNull();
        assertThat(MDC.get(LogFields.IDEMPOTENCY_KEY)).isNull();
    }

    @Test
    void 既存ヘッダがある場合は値を引き継ぎセキュア通信ではHSTSを付与する() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(ApiHeaders.TRACE_ID, "incoming-trace");
        request.addHeader(ApiHeaders.IDEMPOTENCY_KEY, "idem-key");
        request.setSecure(true);
        MockHttpServletResponse response = new MockHttpServletResponse();

        try (MockedStatic<IdGenerator> mocked = Mockito.mockStatic(IdGenerator.class)) {
            mocked.when(IdGenerator::newId).thenReturn("should-not-be-used");

            FilterChain chain = (req, res) -> {
                assertThat(MDC.get(LogFields.TRACE_ID)).isEqualTo("incoming-trace");
                assertThat(MDC.get(LogFields.IDEMPOTENCY_KEY)).isEqualTo("idem-key");
                assertThat(req.getAttribute(LogFields.IDEMPOTENCY_KEY)).isEqualTo("idem-key");
            };

            filter.doFilter(request, response, chain);

            mocked.verifyNoInteractions();
        }

        assertThat(response.getHeader(ApiHeaders.TRACE_ID)).isEqualTo("incoming-trace");
        assertThat(response.getHeader(ApiHeaders.STRICT_TRANSPORT_SECURITY))
                .isEqualTo("max-age=31536000; includeSubDomains");
        assertThat(MDC.get(LogFields.TRACE_ID)).isNull();
        assertThat(MDC.get(LogFields.IDEMPOTENCY_KEY)).isNull();
    }
}
