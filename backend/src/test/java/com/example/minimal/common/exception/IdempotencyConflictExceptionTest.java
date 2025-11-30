package com.example.minimal.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IdempotencyConflictExceptionTest {

    @Test
    void フィールドとエラーコードを保持する() {
        IdempotencyConflictException ex = new IdempotencyConflictException("X-Idempotency-Key", "競合", "IDEMP-0001");

        assertThat(ex.getField()).isEqualTo("X-Idempotency-Key");
        assertThat(ex.getMessage()).isEqualTo("競合");
        assertThat(ex.getErrorCode()).isEqualTo("IDEMP-0001");
    }
}
