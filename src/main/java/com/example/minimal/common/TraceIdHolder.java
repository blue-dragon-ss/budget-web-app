package com.example.minimal.common;

import org.slf4j.MDC;

import com.example.minimal.common.constants.LogFields;

public final class TraceIdHolder {
  private TraceIdHolder() { }

  public static String get() {
    String v = MDC.get(LogFields.TRACE_ID);
    return (v == null) ? "" : v;
  }
}