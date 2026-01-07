package com.example.minimal.item.dto;

import java.math.BigDecimal;

import com.example.minimal.common.constants.Regexes;
import com.example.minimal.common.constants.ValidationConstraints;
import com.example.minimal.common.exception.error.ErrorMessage;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class P202RequestUpdateItem {
	@NotBlank(message = ErrorMessage.VAL_ITEM_ID_NOT_BLANK)
	@Size(min = ValidationConstraints.ULID_LENGTH, max = ValidationConstraints.ULID_LENGTH, message = ErrorMessage.VAL_ITEM_ID_SIZE)
	private String itemId;

	@Size(min = ValidationConstraints.YEAR_MONTH_DATE_LENGTH, max = ValidationConstraints.YEAR_MONTH_DATE_LENGTH, message = ErrorMessage.VAL_YEAR_MONTH_DATE_SIZE)
	@Pattern(regexp = Regexes.YEAR_MONTH_DATE, message = ErrorMessage.VAL_YEAR_MONTH_DATE_PATTERN)
	private String date;

	@Pattern(regexp = Regexes.BLANK_PATTERN, message = ErrorMessage.VAL_TITLE_BLANK_PATTERN) // 入力があるのに空文字をチェック
	@Size(max = ValidationConstraints.TITLE_MAX, message = ErrorMessage.VAL_TITLE_SIZE)
	private String title;

	@Pattern(regexp = Regexes.BLANK_PATTERN, message = ErrorMessage.VAL_PAYER_BLANK_PATTERN) // 入力があるのに空文字をチェック
	@Size(max = ValidationConstraints.PAYER_MAX, message = ErrorMessage.VAL_PAYER_SIZE)
	private String payer;

	@Pattern(regexp = Regexes.BLANK_PATTERN, message = ErrorMessage.VAL_PAYMENT_METHOD_BLANK_PATTERN) // 入力があるのに空文字をチェック
	@Size(max = ValidationConstraints.PAYMENT_METHOD_MAX, message = ErrorMessage.VAL_PAYMENT_METHOD_SIZE)
	private String paymentMethod;

	@DecimalMin(value = ValidationConstraints.MIN_BIGDECIMAL_VALUE_STRING, inclusive = true, message = ErrorMessage.VAL_USAGE_AMOUNT_RANGE)
	@Digits(integer = ValidationConstraints.BIGDECIMAL_INTEGER, fraction = ValidationConstraints.BIGDECIMAL_FRACTION, message = ErrorMessage.VAL_USAGE_AMOUNT_RANGE)
	private BigDecimal usageAmount;

	@DecimalMin(value = ValidationConstraints.MIN_BIGDECIMAL_VALUE_STRING, inclusive = true, message = ErrorMessage.VAL_FEE_AMOUNT_RANGE)
	@Digits(integer = ValidationConstraints.BIGDECIMAL_INTEGER, fraction = ValidationConstraints.BIGDECIMAL_FRACTION, message = ErrorMessage.VAL_FEE_AMOUNT_RANGE)
	private BigDecimal feeAmount;

	@DecimalMin(value = ValidationConstraints.MIN_BIGDECIMAL_VALUE_STRING, inclusive = true, message = ErrorMessage.VAL_TOTAL_AMOUNT_RANGE)
	@Digits(integer = ValidationConstraints.BIGDECIMAL_INTEGER, fraction = ValidationConstraints.BIGDECIMAL_FRACTION, message = ErrorMessage.VAL_TOTAL_AMOUNT_RANGE)
	private BigDecimal totalAmount;

	@DecimalMin(value = ValidationConstraints.MIN_BIGDECIMAL_VALUE_STRING, inclusive = true, message = ErrorMessage.VAL_CURRENT_MONTH_PAID_RANGE)
	@Digits(integer = ValidationConstraints.BIGDECIMAL_INTEGER, fraction = ValidationConstraints.BIGDECIMAL_FRACTION, message = ErrorMessage.VAL_CURRENT_MONTH_PAID_RANGE)
	private BigDecimal currentMonthPaid;

	@DecimalMin(value = ValidationConstraints.MIN_BIGDECIMAL_VALUE_STRING, inclusive = true, message = ErrorMessage.VAL_NEXT_MONTH_PAID_RANGE)
	@Digits(integer = ValidationConstraints.BIGDECIMAL_INTEGER, fraction = ValidationConstraints.BIGDECIMAL_FRACTION, message = ErrorMessage.VAL_NEXT_MONTH_PAID_RANGE)
	private BigDecimal nextMonthPaid;

	private Boolean isNewItem;

	@Min(value = ValidationConstraints.MIN_CATEGORY_ID, message = ErrorMessage.VAL_CATEGORY_ID_MIN)
	@Max(value = ValidationConstraints.MAX_CATEGORY_ID, message = ErrorMessage.VAL_CATEGORY_ID_MAX)
	private Integer categoryId;

	@Size(max = ValidationConstraints.MEMO_MAX, message = ErrorMessage.VAL_MEMO_SIZE)
	private String memo;
}
