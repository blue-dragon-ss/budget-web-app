package com.example.minimal.item;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import com.example.minimal.common.constants.ValidationConstraintCodes;
import com.example.minimal.common.exception.FieldErrorCodeMapper;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.item.dto.P203Request.Fields;

@Component
public class ItemFieldErrorCodeMapper implements FieldErrorCodeMapper {

	@Override
	public boolean supports(FieldError error) {
		if (error == null) {
			return false;
		}
		String field = error.getField();
		return Fields.yearMonth.equals(field) || Fields.itemFile.equals(field);
	}

	@Override
	public Optional<String> resolve(FieldError error) {
		if (error == null) {
			return Optional.empty();
		}
		String field = error.getField();
		String[] constraintCodes = error.getCodes();
		if (Fields.yearMonth.equals(field)) {
			return resolveYearMonth(constraintCodes);
		}
		if (Fields.itemFile.equals(field)) {
			return resolveItemFile(constraintCodes);
		}
		return Optional.empty();
	}

	private Optional<String> resolveYearMonth(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.NOT_BLANK)) {
			return Optional.of(ErrorCode.ITM_VAL_REQUIRED_YEAR_MONTH);
		}
		if (containsConstraint(constraints, ValidationConstraintCodes.SIZE)) {
			return Optional.of(ErrorCode.ITM_VAL_SIZE_YEAR_MONTH);
		}
		if (containsConstraint(constraints, ValidationConstraintCodes.PATTERN)) {
			return Optional.of(ErrorCode.ITM_VAL_PATTERN_YEAR_MONTH);
		}
		return Optional.empty();
	}

	private Optional<String> resolveItemFile(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.NOT_BLANK)) {
			return Optional.of(ErrorCode.ITM_VAL_REQUIRED_ITEM_FILE);
		}
		if (containsConstraint(constraints, ValidationConstraintCodes.PATTERN)) {
			return Optional.of(ErrorCode.ITM_VAL_PATTERN_ITEM_FILE);
		}
		return Optional.empty();
	}

	private boolean containsConstraint(String[] constraints, String expected) {
		if (constraints == null || constraints.length == 0) {
			return false;
		}
		return java.util.Arrays.stream(constraints).filter(java.util.Objects::nonNull)
				.anyMatch(code -> code.equals(expected) || code.startsWith(expected + "."));
	}
}
