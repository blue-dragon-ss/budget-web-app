package com.example.minimal.common.exception;

import lombok.Getter;

public class DuplicateValueException extends RuntimeException {
  @Getter
  private final String field;
  @Getter
  private final String errorCode; // 例: VAL-0105

  public DuplicateValueException(String field, String message, String errorCode) {
    super(message);
    this.field = field;
    this.errorCode = errorCode;
  }
}
