package com.example.minimal.item.dto;

import java.util.List;

import com.example.minimal.common.constants.Regexes;
import com.example.minimal.common.constants.ValidationConstraints;
import com.example.minimal.common.exception.error.ErrorMessage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class P202Request {
	@NotBlank(message = ErrorMessage.VAL_YEAR_MONTH_NOT_BLANK)
	@Size(min = ValidationConstraints.YEAR_MONTH_LENGTH, max = ValidationConstraints.YEAR_MONTH_LENGTH, message = ErrorMessage.VAL_YEAR_MONTH_SIZE)
	@Pattern(regexp = Regexes.YEAR_MONTH, message = ErrorMessage.VAL_YEAR_MONTH_PATTERN)
	private String yearMonth;

	@Min(value = ValidationConstraints.MIN_UPDATE_TOTAL_NUM, message = ErrorMessage.VAL_UPDATE_TOTAL_NUM_MIN)
	@Max(value = ValidationConstraints.MAX_UPDATE_TOTAL_NUM, message = ErrorMessage.VAL_UPDATE_TOTAL_NUM_OVER)
	private int totalNum;

	@NotNull(message = ErrorMessage.VAL_UPDATE_ITEM_LIST_NULL)
	@Size(min = ValidationConstraints.MIN_UPDATE_ITEM_LIST_SIZE, max = ValidationConstraints.MAX_UPDATE_ITEM_LIST_SIZE, message = ErrorMessage.VAL_UPDATE_ITEM_LIST_SIZE)
	private List<@NotNull(message = ErrorMessage.VAL_UPDATE_ITEM_NULL) @Valid P202RequestUpdateItem> updateItemList;
}