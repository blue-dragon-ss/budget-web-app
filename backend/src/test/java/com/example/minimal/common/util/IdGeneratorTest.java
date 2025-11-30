package com.example.minimal.common.util;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class IdGeneratorTest {

	@Test
	void コンストラクタ() {
		new IdGenerator();
	}

	@Test
	void UUID形式の文字列が生成される() {
		String actual = IdGenerator.newId();

		assertThatCode(() -> UUID.fromString(actual)).doesNotThrowAnyException();
	}
}
