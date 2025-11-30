package com.example.minimal.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.security.MessageDigest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;

import com.example.minimal.common.exception.error.CryptoOperationException;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.common.exception.error.ErrorMessage;

class StringUtilsTest {

	@Test
	void コンストラクタ() {
		new StringUtils();
	}

	@ParameterizedTest
	@NullSource
	void trimはnull安全にトリムする(String input) {
		assertThat(StringUtils.trim(input)).isNull();
	}

	@ParameterizedTest
	@EmptySource
	@ValueSource(strings = { "a", "abc", // 通常の文字列
			"  a", // 先頭に空白
			"a  ", // 末尾に空白
			"テキスト", // 日本語
			"🙂", // 絵文字（isBlankはfalse）
			" ", // 半角スペース
			"\t", // タブ
			"\n", // LF
			"\r\n", // CRLF
	})
	void trimはnull以外をトリムする(String input) {
		assertThat(StringUtils.trim(input)).isEqualTo(input.trim());
	}

	@ParameterizedTest
	@NullSource
	void normalizeEmailはnullをそのまま返す(String input) {
		assertThat(StringUtils.normalizeEmail(input)).isNull();
	}

	@ParameterizedTest
	@EmptySource
	@ValueSource(strings = { " ", // 半角スペース
			"\t", // タブ
			"\n", // LF
			"\r\n", // CRLF
	})
	void normalizeEmailは空白をnullにする(String input) {
		assertThat(StringUtils.normalizeEmail(input)).isNull();
	}

	@ParameterizedTest
	@ValueSource(strings = { "USER@example.com", // 大文字含む
			"  USER@example.com", // 先頭に空白
			"USER@example.com  ", // 末尾に空白
			"テキスト", // 日本語
			"🙂", // 絵文字
	})
	void normalizeEmailはトリムと小文字化を行う(String input) {
		assertThat(StringUtils.normalizeEmail(input)).isEqualTo(input.trim().toLowerCase());
	}

	@ParameterizedTest
	@NullSource
	void normalizeIdempotencyKeyはnullをそのまま返す(String input) {
		assertThat(StringUtils.normalizeIdempotencyKey(null)).isNull();
	}

	@ParameterizedTest
	@ValueSource(strings = { "Key-123", // 大文字含む
			"  Key-123", // 先頭に空白
			"Key-123  ", // 末尾に空白
			"テキスト", // 日本語
			"🙂", // 絵文字
	})
	void normalizeIdempotencyKeyはトリムと小文字化を行う(String input) {
		assertThat(StringUtils.normalizeIdempotencyKey(input)).isEqualTo(input.trim().toLowerCase());
	}

	@ParameterizedTest
	@EmptySource
	@ValueSource(strings = { " ", // 半角スペース
			"\t", // タブ
			"\n", // LF
			"\r\n", // CRLF
	})
	void normalizeIdempotencyKeyは空文字をnullにする(String input) {
		assertThat(StringUtils.normalizeIdempotencyKey(input)).isNull();
	}

	@ParameterizedTest
	@ValueSource(strings = { "/Members/", // 末尾スラッシュあり
			" /Members/ ", // 先頭と末尾に空白と末尾スラッシュあり
			"/API/v1/Endpoint/", // 大文字と末尾スラッシュあり
			"/api/v1/endpoint/", // 末尾スラッシュあり
			" /api/v1/endpoint/ ", // 先頭と末尾に空白と末尾スラッシュあり
	})
	void normalizeEndpointは末尾スラッシュを取り除き小文字化する(String input) {
		assertThat(StringUtils.normalizeEndpoint(input)).isEqualTo(input.trim().toLowerCase().replaceAll("/+$", ""));
	}

	@Test
	void normalizeEndpointはルートパスのみの場合nullを返す() {
		assertThat(StringUtils.normalizeEndpoint("/")).isEqualTo("/");
		assertThat(StringUtils.normalizeEndpoint(" / ")).isEqualTo("/");
	}

	@ParameterizedTest
	@NullSource
	void normalizeEndpointはnullをそのまま返す(String input) {
		assertThat(StringUtils.normalizeEndpoint(null)).isNull();
	}

	@ParameterizedTest
	@EmptySource
	@ValueSource(strings = { " ", // 半角スペース
			"\t", // タブ
			"\n", // LF
			"\r\n", // CRLF
	})
	void normalizeEndpointは空白のみをnullにする(String input) {
		assertThat(StringUtils.normalizeEndpoint(input)).isNull();
	}

	@ParameterizedTest
	@NullSource
	void safeはnullを空文字に変換する(String input) {
		assertThat(StringUtils.safe(input)).isEqualTo("");
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "test", "  test  ", "テキスト", "🙂" })
	void safeはnull以外をそのまま返す(String input) {
		assertThat(StringUtils.safe(input)).isEqualTo(input);
	}

	@Test
	void sha256は期待通りのハッシュ値を返す() {
		assertThat(StringUtils.sha256("test"))
				.isEqualTo("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08");
	}

	@Test
	void sha256はMessageDigestのgetInstanceで例外が投げられたとき該当のエラーメッセージ等を返す() {
		try (MockedStatic<MessageDigest> mocked = mockStatic(MessageDigest.class)) {
			mocked.when(() -> MessageDigest.getInstance("SHA-256"))
					.thenThrow(new java.security.NoSuchAlgorithmException("SHA-256 not found"));

			CryptoOperationException ex = assertThrows(CryptoOperationException.class, () -> {
				StringUtils.sha256("test"); // テスト対象呼び出し
			});

			assertEquals(ErrorMessage.SHA256_ALGORITHM_NOT_FOUND, ex.getMessage());
			assertEquals(ErrorCode.COM_SHA256_ALGORITHM_NOT_FOUND, ex.getErrorCode());
			assertTrue(ex.getCause() instanceof java.security.NoSuchAlgorithmException);
		}
	}
}
