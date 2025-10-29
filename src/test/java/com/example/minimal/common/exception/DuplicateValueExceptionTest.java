package com.example.minimal.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DuplicateValueExceptionTest {

    @Test
    void フィールドとメッセージを保持する() {
        DuplicateValueException ex = new DuplicateValueException("code", "重複", "VAL-0105");

        assertThat(ex.getField()).isEqualTo("code");
        assertThat(ex.getMessage()).isEqualTo("重複");
        assertThat(ex.getErrorCode()).isEqualTo("VAL-0105");
    }
}
