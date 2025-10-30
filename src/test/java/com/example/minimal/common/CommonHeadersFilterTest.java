package com.example.minimal.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.example.minimal.common.constants.ApiHeaders;
import com.example.minimal.common.constants.LogFields;
import com.example.minimal.common.util.IdGenerator;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

class CommonHeadersFilterTest {

    private final CommonHeadersFilter filter = new CommonHeadersFilter();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void whenTraceIdHeaderIsBlank_generatesNewTraceIdAndSkipsEmptyIdempotencyKey() throws Exception {
        try (MockedStatic<IdGenerator> mockedIdGenerator = Mockito.mockStatic(IdGenerator.class)) {
            String generatedTraceId = "generated-trace-id";
            mockedIdGenerator.when(IdGenerator::newId).thenReturn(generatedTraceId);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(ApiHeaders.TRACE_ID, "");
            request.addHeader(ApiHeaders.IDEMPOTENCY_KEY, "");

            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilter(request, response, new MockFilterChain() {
                @Override
                public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse)
                        throws IOException, ServletException {
                    assertThat(servletRequest.getAttribute(LogFields.TRACE_ID)).isEqualTo(generatedTraceId);
                    assertThat(servletRequest.getAttribute(LogFields.IDEMPOTENCY_KEY)).isNull();
                    assertThat(MDC.get(LogFields.TRACE_ID)).isEqualTo(generatedTraceId);
                    assertThat(MDC.get(LogFields.IDEMPOTENCY_KEY)).isNull();
                    super.doFilter(servletRequest, servletResponse);
                }
            });

            mockedIdGenerator.verify(IdGenerator::newId);
            assertThat(response.getHeader(ApiHeaders.TRACE_ID)).isEqualTo(generatedTraceId);
            assertThat(request.getAttribute(LogFields.IDEMPOTENCY_KEY)).isNull();
            assertThat(MDC.get(LogFields.TRACE_ID)).isNull();
            assertThat(MDC.get(LogFields.IDEMPOTENCY_KEY)).isNull();
        }
    }
}
