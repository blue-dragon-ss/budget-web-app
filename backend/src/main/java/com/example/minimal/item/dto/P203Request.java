package com.example.minimal.item.dto;

import org.springframework.web.multipart.MultipartFile;

import com.example.minimal.common.constants.Regexes;
import com.example.minimal.common.constants.ValidationConstraints;
import com.example.minimal.common.exception.error.ErrorMessage;

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
public class P203Request {
	@NotBlank(message = ErrorMessage.VAL_YEAR_MONTH_NOT_BLANK)
	@Size(min = ValidationConstraints.YEAR_MONTH_LENGTH, max = ValidationConstraints.YEAR_MONTH_LENGTH, message = ErrorMessage.VAL_CODE_SIZE)
	@Pattern(regexp = Regexes.YEAR_MONTH, message = ErrorMessage.VAL_YEAR_MONTH_PATTERN)
	private String yearMonth;

	@NotBlank(message = ErrorMessage.VAL_ITEM_FILE_NOT_BLANK)
	@Pattern(regexp = Regexes.CSV_FILE_EXTENSION, message = ErrorMessage.VAL_ITEM_FILE_PATTERN)
	private MultipartFile itemFile;
}
