package com.example.minimal.member;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import com.example.minimal.common.constants.ValidationConstraintCodes;
import com.example.minimal.common.exception.FieldErrorCodeMapper;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.member.dto.CreateMemberRequest.Fields;

@Component
public class MemberFieldErrorCodeMapper implements FieldErrorCodeMapper {

    @Override
    public boolean supports(FieldError error) {
        if (error == null) {
            return false;
        }
        String field = error.getField();
        return Fields.code.equals(field)
                || Fields.name.equals(field)
                || Fields.email.equals(field)
                || Fields.note.equals(field);
    }

    @Override
    public Optional<String> resolve(FieldError error) {
        if (error == null) {
            return Optional.empty();
        }
        String field = error.getField();
        String constraint = error.getCode();
        if (Fields.code.equals(field)) {
            return resolveCode(constraint);
        }
        if (Fields.name.equals(field)) {
            return resolveName(constraint);
        }
        if (Fields.email.equals(field)) {
            return resolveEmail(constraint);
        }
        if (Fields.note.equals(field)) {
            return resolveNote(constraint);
        }
        return Optional.empty();
    }

    private Optional<String> resolveCode(String constraint) {
        if (ValidationConstraintCodes.NOT_BLANK.equals(constraint)) {
            return Optional.of(ErrorCode.MBR_VAL_REQUIRED_CODE);
        }
        if (ValidationConstraintCodes.SIZE.equals(constraint)) {
            return Optional.of(ErrorCode.MBR_VAL_SIZE_CODE);
        }
        return Optional.empty();
    }

    private Optional<String> resolveName(String constraint) {
        if (ValidationConstraintCodes.NOT_BLANK.equals(constraint)) {
            return Optional.of(ErrorCode.MBR_VAL_REQUIRED_NAME);
        }
        if (ValidationConstraintCodes.SIZE.equals(constraint)) {
            return Optional.of(ErrorCode.MBR_VAL_SIZE_NAME);
        }
        return Optional.empty();
    }

    private Optional<String> resolveEmail(String constraint) {
        if (ValidationConstraintCodes.SIZE.equals(constraint)) {
            return Optional.of(ErrorCode.MBR_VAL_SIZE_EMAIL);
        }
        if (ValidationConstraintCodes.PATTERN.equals(constraint)) {
            return Optional.of(ErrorCode.MBR_VAL_FORMAT_EMAIL);
        }
        return Optional.empty();
    }

    private Optional<String> resolveNote(String constraint) {
        if (ValidationConstraintCodes.SIZE.equals(constraint)) {
            return Optional.of(ErrorCode.MBR_VAL_SIZE_NOTE);
        }
        return Optional.empty();
    }
}
