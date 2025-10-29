package com.example.minimal.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void trimはnull安全にトリムする() {
        assertThat(StringUtils.trim("  abc  ")).isEqualTo("abc");
        assertThat(StringUtils.trim(null)).isNull();
    }

    @Test
    void normalizeEmailはトリムと小文字化を行い空白はnullにする() {
        assertThat(StringUtils.normalizeEmail("  USER@example.com  ")).isEqualTo("user@example.com");
        assertThat(StringUtils.normalizeEmail(" ")).isNull();
    }

    @Test
    void normalizeIdempotencyKeyはトリム小文字化し空はnullにする() {
        assertThat(StringUtils.normalizeIdempotencyKey("  Key-123  ")).isEqualTo("key-123");
        assertThat(StringUtils.normalizeIdempotencyKey("   ")).isNull();
        assertThat(StringUtils.normalizeIdempotencyKey(null)).isNull();
    }

    @Test
    void normalizeEndpointは末尾スラッシュを取り除き小文字化する() {
        assertThat(StringUtils.normalizeEndpoint(" /Members/ ")).isEqualTo("/members");
        assertThat(StringUtils.normalizeEndpoint("/"))
                .isEqualTo("/");
        assertThat(StringUtils.normalizeEndpoint("   ")).isNull();
    }

    @Test
    void safeはnullを空文字に変換する() {
        assertThat(StringUtils.safe(null)).isEqualTo("");
        assertThat(StringUtils.safe("value")).isEqualTo("value");
    }

    @Test
    void sha256は期待通りのハッシュ値を返す() {
        assertThat(StringUtils.sha256("test"))
                .isEqualTo("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08");
    }
}
