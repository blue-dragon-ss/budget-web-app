package com.example.minimal.member.dto;

import com.example.minimal.common.constants.Regexes;
import com.example.minimal.common.constants.ValidationConstraints;
import com.example.minimal.common.exception.error.ErrorMessage;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class CreateMemberRequest {
  @NotBlank(message = ErrorMessage.VAL_CODE_NOT_BLANK)
  @Size(min = ValidationConstraints.CODE_MIN, max = ValidationConstraints.CODE_MAX, message = ErrorMessage.VAL_CODE_SIZE)
  private String code;

  @NotBlank(message = ErrorMessage.VAL_NAME_NOT_BLANK)
  @Size(min = ValidationConstraints.NAME_MIN, max = ValidationConstraints.NAME_MAX, message = ErrorMessage.VAL_NAME_SIZE)
  private String name;

  @Size(max = ValidationConstraints.EMAIL_MAX, message = ErrorMessage.VAL_EMAIL_SIZE)
  @Pattern(
    regexp = Regexes.EMAIL,
    message = ErrorMessage.VAL_EMAIL_PATTERN
  )
  private String email;

  @Size(max = ValidationConstraints.NOTE_MAX, message = ErrorMessage.VAL_NOTE_SIZE)
  private String note;
}
