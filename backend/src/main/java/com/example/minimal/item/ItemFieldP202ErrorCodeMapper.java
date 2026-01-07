package com.example.minimal.item;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import com.example.minimal.common.constants.ValidationConstraintCodes;
import com.example.minimal.common.exception.FieldErrorCodeMapper;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.item.dto.P202Request.Fields;

@Component
public class ItemFieldP202ErrorCodeMapper implements FieldErrorCodeMapper {

	@Override
	public boolean supports(FieldError error) {
		if (error == null) {
			return false;
		}
		String field = error.getField();
		return Fields.yearMonth.equals(field) || Fields.totalNum.equals(field) || Fields.updateItemList.equals(field);
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
		if (Fields.totalNum.equals(field)) {
			return resolveTotalNum(constraintCodes);
		}
		if (Fields.updateItemList.equals(field)) {
			return resolveUpdateItemList(constraintCodes);
		}
		if (field.contains(Fields.updateItemList)) {
			return resolveUpdateItem(constraintCodes);
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

	private Optional<String> resolveTotalNum(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.MIN)
				|| containsConstraint(constraints, ValidationConstraintCodes.MAX)) {
			return Optional.of(ErrorCode.ITM_VAL_SIZE_TOTAL_NUM);
		}
		return Optional.empty();
	}

	private Optional<String> resolveUpdateItemList(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.NOT_NULL)) {
			return Optional.of(ErrorCode.ITM_VAL_REQUIRED_UPDATE_ITEM_LIST);
		}
		if (containsConstraint(constraints, ValidationConstraintCodes.SIZE)) {
			return Optional.of(ErrorCode.ITM_VAL_SIZE_UPDATE_ITEM_LIST);
		}
		return Optional.empty();
	}

	private Optional<String> resolveUpdateItem(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.NOT_NULL)) {
			return Optional.of(ErrorCode.ITM_VAL_REQUIRED_UPDATE_ITEM);
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
