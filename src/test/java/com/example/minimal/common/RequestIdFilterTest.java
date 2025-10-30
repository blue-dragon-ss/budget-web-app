package com.example.minimal.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

			FilterChain chain = (_, _) -> {
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
			FilterChain chain = (_, _) -> assertThat(MDC.get(LogFields.RID)).isEqualTo("incoming-rid");

			filter.doFilter(request, response, chain);

			mocked.verifyNoInteractions();
		}

		assertThat(response.getHeader(ApiHeaders.REQUEST_ID)).isEqualTo("incoming-rid");
		assertThat(MDC.get(LogFields.RID)).isNull();
	}

        @Test
        void 空のリクエストIDヘッダの場合は新しいIDを生成して設定する() throws Exception {
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
