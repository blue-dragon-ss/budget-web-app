package com.example.minimal.common.constants;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

public class ValidationConstraintsTest {

	@Test
	void 定数が正しく設定されている() {
		assertThat(ValidationConstraints.CODE_MIN).isEqualTo(1);
		assertThat(ValidationConstraints.CODE_MAX).isEqualTo(50);
		assertThat(ValidationConstraints.NAME_MIN).isEqualTo(1);
		assertThat(ValidationConstraints.NAME_MAX).isEqualTo(200);
		assertThat(ValidationConstraints.EMAIL_MAX).isEqualTo(320);
		assertThat(ValidationConstraints.NOTE_MAX).isEqualTo(10000);
		assertThat(ValidationConstraints.ULID_LENGTH).isEqualTo(26);
		assertThat(ValidationConstraints.HTTP_CLIENT_TIMEOUT).isEqualTo(Duration.ofSeconds(5));
		assertThat(ValidationConstraints.MAX_RETRY).isEqualTo(3);
	}
}
