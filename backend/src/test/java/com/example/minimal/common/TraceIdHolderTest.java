package com.example.minimal.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import com.example.minimal.common.constants.LogFields;

class TraceIdHolderTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void MDCに値が存在する場合はそのまま返す() {
        MDC.put(LogFields.TRACE_ID, "trace-123");

        assertThat(TraceIdHolder.get()).isEqualTo("trace-123");
    }

    @Test
    void MDCに値が無い場合は空文字を返す() {
        MDC.remove(LogFields.TRACE_ID);

        assertThat(TraceIdHolder.get()).isEmpty();
    }
}
