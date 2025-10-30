package com.example.minimal.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.MDC;

import com.example.minimal.common.constants.ApiHeaders;
import com.example.minimal.common.constants.LogFields;
import com.example.minimal.common.util.IdGenerator;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class RequestIdFilterTest {

    private final RequestIdFilter filter = new RequestIdFilter();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldGenerateNewRequestIdWhenHeaderIsBlank() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(request.getHeader(ApiHeaders.REQUEST_ID)).thenReturn("");

        String generatedId = "generated-request-id";
        try (MockedStatic<IdGenerator> mockedIdGenerator = Mockito.mockStatic(IdGenerator.class)) {
            mockedIdGenerator.when(IdGenerator::newId).thenReturn(generatedId);

            filter.doFilterInternal(request, response, filterChain);

            mockedIdGenerator.verify(IdGenerator::newId);
            verify(response).setHeader(ApiHeaders.REQUEST_ID, generatedId);
            verify(filterChain).doFilter(request, response);
            assertThat(MDC.get(LogFields.RID)).isNull();
        }
    }
}
