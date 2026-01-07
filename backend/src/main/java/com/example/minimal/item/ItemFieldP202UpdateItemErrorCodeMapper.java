package com.example.minimal.item;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import com.example.minimal.common.constants.ValidationConstraintCodes;
import com.example.minimal.common.exception.FieldErrorCodeMapper;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.item.dto.P202RequestUpdateItem.Fields;

@Component
public class ItemFieldP202UpdateItemErrorCodeMapper implements FieldErrorCodeMapper {

	@Override
	public boolean supports(FieldError error) {
		if (error == null) {
			return false;
		}
		String field = error.getField();
		if (field != null) {
			field = field.replaceAll("^.*\\.", "");
		}
		return Fields.itemId.equals(field) || Fields.date.equals(field) || Fields.title.equals(field)
				|| Fields.payer.equals(field) || Fields.paymentMethod.equals(field) || Fields.usageAmount.equals(field)
				|| Fields.feeAmount.equals(field) || Fields.totalAmount.equals(field)
				|| Fields.currentMonthPaid.equals(field) || Fields.nextMonthPaid.equals(field)
				|| Fields.categoryId.equals(field) || Fields.memo.equals(field);
	}

	@Override
	public Optional<String> resolve(FieldError error) {
		if (error == null) {
			return Optional.empty();
		}
		String field = error.getField();
		if (field != null) {
			field = field.replaceAll("^.*\\.", "");
		}
		String[] constraintCodes = error.getCodes();
		if (Fields.itemId.equals(field)) {
			return resolveItemId(constraintCodes);
		}
		if (Fields.date.equals(field)) {
			return resolveDate(constraintCodes);
		}
		if (Fields.title.equals(field)) {
			return resolveTitle(constraintCodes);
		}
		if (Fields.payer.equals(field)) {
			return resolvePayer(constraintCodes);
		}
		if (Fields.paymentMethod.equals(field)) {
			return resolvePaymentMethod(constraintCodes);
		}
		if (Fields.usageAmount.equals(field)) {
			return resolveUsageAmount(constraintCodes);
		}
		if (Fields.feeAmount.equals(field)) {
			return resolveFeeAmount(constraintCodes);
		}
		if (Fields.totalAmount.equals(field)) {
			return resolveTotalAmount(constraintCodes);
		}
		if (Fields.currentMonthPaid.equals(field)) {
			return resolveCurrentMonthPaid(constraintCodes);
		}
		if (Fields.nextMonthPaid.equals(field)) {
			return resolveNextMonthPaid(constraintCodes);
		}
		if (Fields.categoryId.equals(field)) {
			return resolveCategoryId(constraintCodes);
		}
		if (Fields.memo.equals(field)) {
			return resolveMemo(constraintCodes);
		}
		return Optional.empty();
	}

	private Optional<String> resolveItemId(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.NOT_BLANK)) {
			return Optional.of(ErrorCode.ITM_VAL_REQUIRED_ITEM_ID);
		}
		if (containsConstraint(constraints, ValidationConstraintCodes.SIZE)) {
			return Optional.of(ErrorCode.ITM_VAL_SIZE_ITEM_ID);
		}
		return Optional.empty();
	}

	private Optional<String> resolveDate(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.SIZE)) {
			return Optional.of(ErrorCode.ITM_VAL_SIZE_DATE);
		}
		if (containsConstraint(constraints, ValidationConstraintCodes.PATTERN)) {
			return Optional.of(ErrorCode.ITM_VAL_PATTERN_DATE);
		}
		return Optional.empty();
	}

	private Optional<String> resolveTitle(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.SIZE)) {
			return Optional.of(ErrorCode.ITM_VAL_SIZE_TITLE);
		}
		if (containsConstraint(constraints, ValidationConstraintCodes.PATTERN)) {
			return Optional.of(ErrorCode.ITM_VAL_PATTERN_TITLE);
		}
		return Optional.empty();
	}

	private Optional<String> resolvePayer(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.SIZE)) {
			return Optional.of(ErrorCode.ITM_VAL_SIZE_PAYER);
		}
		if (containsConstraint(constraints, ValidationConstraintCodes.PATTERN)) {
			return Optional.of(ErrorCode.ITM_VAL_PATTERN_PAYER);
		}
		return Optional.empty();
	}

	private Optional<String> resolvePaymentMethod(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.SIZE)) {
			return Optional.of(ErrorCode.ITM_VAL_SIZE_PAYMENT_METHOD);
		}
		if (containsConstraint(constraints, ValidationConstraintCodes.PATTERN)) {
			return Optional.of(ErrorCode.ITM_VAL_PATTERN_PAYMENT_METHOD);
		}
		return Optional.empty();
	}

	private Optional<String> resolveUsageAmount(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.DecimalMin)
				|| containsConstraint(constraints, ValidationConstraintCodes.Digits)) {
			return Optional.of(ErrorCode.ITM_VAL_RANGE_USAGE_AMOUNT);
		}
		return Optional.empty();
	}

	private Optional<String> resolveFeeAmount(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.DecimalMin)
				|| containsConstraint(constraints, ValidationConstraintCodes.Digits)) {
			return Optional.of(ErrorCode.ITM_VAL_RANGE_FEE_AMOUNT);
		}
		return Optional.empty();
	}

	private Optional<String> resolveTotalAmount(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.DecimalMin)
				|| containsConstraint(constraints, ValidationConstraintCodes.Digits)) {
			return Optional.of(ErrorCode.ITM_VAL_RANGE_TOTAL_AMOUNT);
		}
		return Optional.empty();
	}

	private Optional<String> resolveCurrentMonthPaid(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.DecimalMin)
				|| containsConstraint(constraints, ValidationConstraintCodes.Digits)) {
			return Optional.of(ErrorCode.ITM_VAL_RANGE_CURRENT_MONTH_PAID);
		}
		return Optional.empty();
	}

	private Optional<String> resolveNextMonthPaid(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.DecimalMin)
				|| containsConstraint(constraints, ValidationConstraintCodes.Digits)) {
			return Optional.of(ErrorCode.ITM_VAL_RANGE_NEXT_MONTH_PAID);
		}
		return Optional.empty();
	}

	private Optional<String> resolveCategoryId(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.MIN)
				|| containsConstraint(constraints, ValidationConstraintCodes.MAX)) {
			return Optional.of(ErrorCode.ITM_VAL_RANGE_CATEGORY_ID);
		}
		return Optional.empty();
	}

	private Optional<String> resolveMemo(String[] constraints) {
		if (containsConstraint(constraints, ValidationConstraintCodes.SIZE)) {
			return Optional.of(ErrorCode.ITM_VAL_SIZE_MEMO);
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
