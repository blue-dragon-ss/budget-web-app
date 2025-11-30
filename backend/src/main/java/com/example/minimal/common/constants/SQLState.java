package com.example.minimal.common.constants;

public enum SQLState {

  STRING_DATA_RIGHT_TRUNCATION("22001"),
  NOT_NULL_VIOLATION("23502"),
  FOREIGN_KEY_VIOLATION("23503"),
  UNIQUE_VIOLATION("23505"),
  CHECK_VIOLATION("23514");
  

  private final String code;

  SQLState(String code) {
	this.code = code;
  }

  public String getCode() {
	return code;
  }
}
