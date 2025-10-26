package com.example.minimal.common;

import org.slf4j.MDC;

public final class TraceIdHolder {
  private static final String KEY = "traceId";
  private TraceIdHolder() { }

  public static String get() {
    String v = MDC.get(KEY);
    return (v == null) ? "" : v;
  }
}