package com.example.minimal.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;

class SQLUtilsTest {

	@Test
	void コンストラクタ() {
		new SQLUtils();
	}

	@Test
	void 制約名が一致する場合は一意制約違反と判定する() {
		ConstraintViolationException violation = new ConstraintViolationException("msg", null, "uk_members_code");
		RuntimeException wrapper = new RuntimeException(violation);

		boolean actual = SQLUtils.isUniqueViolation(wrapper, "UK_MEMBERS_CODE");

		assertThat(actual).isTrue();
	}

	@Test
	void SQLStateが一致する場合は一意制約違反と判定する() {
		SQLException sqlException = new SQLException("msg", "23505");
		ConstraintViolationException violation = new ConstraintViolationException("msg", sqlException, null);

		boolean actual = SQLUtils.isUniqueViolation(violation, "other");

		assertThat(actual).isTrue();
	}

	@Test
	void 該当しない場合は一意制約違反ではない() {
		ConstraintViolationException violation = new ConstraintViolationException("msg", null, "uk_members_code");

		boolean actual = SQLUtils.isUniqueViolation(violation, "uk_other");

		assertThat(actual).isFalse();
	}
}
