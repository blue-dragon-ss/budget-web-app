package com.example.minimal.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

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

class RequestIdFilterTest {

    private final RequestIdFilter filter = new RequestIdFilter();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void リクエストヘッダが無い場合は生成したIDを利用する() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        try (MockedStatic<IdGenerator> mocked = Mockito.mockStatic(IdGenerator.class)) {
            mocked.when(IdGenerator::newId).thenReturn("generated-request-id");

            FilterChain chain = (req, res) -> {
                assertThat(MDC.get(LogFields.RID)).isEqualTo("generated-request-id");
            };

            filter.doFilter(request, response, chain);

            mocked.verify(IdGenerator::newId, Mockito.times(1));
        }

        assertThat(response.getHeader(ApiHeaders.REQUEST_ID)).isEqualTo("generated-request-id");
        assertThat(MDC.get(LogFields.RID)).isNull();
    }

    @Test
    void 既存ヘッダがある場合はそのまま利用しMDCを解放する() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(ApiHeaders.REQUEST_ID, "incoming-rid");
        MockHttpServletResponse response = new MockHttpServletResponse();

        try (MockedStatic<IdGenerator> mocked = Mockito.mockStatic(IdGenerator.class)) {
            FilterChain chain = (req, res) -> assertThat(MDC.get(LogFields.RID)).isEqualTo("incoming-rid");

            filter.doFilter(request, response, chain);

            mocked.verifyNoInteractions();
        }

        assertThat(response.getHeader(ApiHeaders.REQUEST_ID)).isEqualTo("incoming-rid");
        assertThat(MDC.get(LogFields.RID)).isNull();
    }
}
