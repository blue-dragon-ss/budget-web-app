package com.example.minimal.common.util;

import org.hibernate.exception.ConstraintViolationException;

import com.example.minimal.common.constants.SQLState;

public class SQLUtils {
	/**
	 * 一意制約違反かどうかを判定する
	 * 
	 * @param t
	 * @param constraintName
	 * @return true: 一意制約違反、false: それ以外
	 */
	public static boolean isUniqueViolation(Throwable t, String constraintName) {
		for (Throwable cur = t; cur != null; cur = cur.getCause()) {
			if (cur instanceof ConstraintViolationException cve) {
				// 1) 制約名での一致
				String name = cve.getConstraintName();
				if (name != null && name.equalsIgnoreCase(constraintName)) {
					return true;
				}
				// 2) SQLState（Hibernate が抱える SQLException から取得）
				String state = (cve.getSQLException() != null) ? cve.getSQLException().getSQLState() : null;
				if (SQLState.UNIQUE_VIOLATION.getCode().equals(state)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 外部キー制約違反かどうかを判定する
	 * 
	 * @param t
	 * @param constraintName
	 * @return true: 外部キー制約違反、false: それ以外
	 */
	public static boolean isForeignKeyViolation(Throwable t, String constraintName) {
		for (Throwable cur = t; cur != null; cur = cur.getCause()) {
			if (cur instanceof ConstraintViolationException cve) {
				// 1) 制約名での一致
				String name = cve.getConstraintName();
				if (name != null && name.equalsIgnoreCase(constraintName)) {
					return true;
				}
				// 2) SQLState（Hibernate が抱える SQLException から取得）
				String state = (cve.getSQLException() != null) ? cve.getSQLException().getSQLState() : null;
				if (SQLState.FOREIGN_KEY_VIOLATION.getCode().equals(state)) {
					return true;
				}
			}
		}
		return false;
	}
}
