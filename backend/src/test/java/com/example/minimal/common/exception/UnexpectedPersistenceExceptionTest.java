package com.example.minimal.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UnexpectedPersistenceExceptionTest {

    @Test
    void フィールドと原因例外を保持する() {
        RuntimeException cause = new RuntimeException("root");
        UnexpectedPersistenceException ex = new UnexpectedPersistenceException("code", "失敗", "SYS-0001", cause);

        assertThat(ex.getField()).isEqualTo("code");
        assertThat(ex.getMessage()).isEqualTo("失敗");
        assertThat(ex.getErrorCode()).isEqualTo("SYS-0001");
        assertThat(ex.getCause()).isSameAs(cause);
    }
}
