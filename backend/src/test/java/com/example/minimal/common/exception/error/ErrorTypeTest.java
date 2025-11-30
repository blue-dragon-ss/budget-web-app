package com.example.minimal.common.exception.error;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ErrorTypeTest {
	@Test
	void 各項目の整合性を確認する() {
		ErrorType validation = ErrorType.VALIDATION;
		assertThat(validation).isEqualTo(ErrorType.VALIDATION);

		ErrorType notFound = ErrorType.NOT_FOUND;
		assertThat(notFound).isEqualTo(ErrorType.NOT_FOUND);

		ErrorType conflict = ErrorType.CONFLICT;
		assertThat(conflict).isEqualTo(ErrorType.CONFLICT);

		ErrorType unauthorized = ErrorType.UNAUTHORIZED;
		assertThat(unauthorized).isEqualTo(ErrorType.UNAUTHORIZED);

		ErrorType forbidden = ErrorType.FORBIDDEN;
		assertThat(forbidden).isEqualTo(ErrorType.FORBIDDEN);

		ErrorType serverError = ErrorType.SERVER_ERROR;
		assertThat(serverError).isEqualTo(ErrorType.SERVER_ERROR);
	}
}
