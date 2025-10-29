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
        String[] constraintCodes = error.getCodes();
        if (Fields.code.equals(field)) {
            return resolveCode(constraintCodes);
        }
        if (Fields.name.equals(field)) {
            return resolveName(constraintCodes);
        }
        if (Fields.email.equals(field)) {
            return resolveEmail(constraintCodes);
        }
        if (Fields.note.equals(field)) {
            return resolveNote(constraintCodes);
        }
        return Optional.empty();
    }

    private Optional<String> resolveCode(String[] constraints) {
        if (containsConstraint(constraints, ValidationConstraintCodes.NOT_BLANK)) {
            return Optional.of(ErrorCode.MBR_VAL_REQUIRED_CODE);
        }
        if (containsConstraint(constraints, ValidationConstraintCodes.SIZE)) {
            return Optional.of(ErrorCode.MBR_VAL_SIZE_CODE);
        }
        return Optional.empty();
    }

    private Optional<String> resolveName(String[] constraints) {
        if (containsConstraint(constraints, ValidationConstraintCodes.NOT_BLANK)) {
            return Optional.of(ErrorCode.MBR_VAL_REQUIRED_NAME);
        }
        if (containsConstraint(constraints, ValidationConstraintCodes.SIZE)) {
            return Optional.of(ErrorCode.MBR_VAL_SIZE_NAME);
        }
        return Optional.empty();
    }

    private Optional<String> resolveEmail(String[] constraints) {
        if (containsConstraint(constraints, ValidationConstraintCodes.SIZE)) {
            return Optional.of(ErrorCode.MBR_VAL_SIZE_EMAIL);
        }
        if (containsConstraint(constraints, ValidationConstraintCodes.PATTERN)) {
            return Optional.of(ErrorCode.MBR_VAL_FORMAT_EMAIL);
        }
        return Optional.empty();
    }

    private Optional<String> resolveNote(String[] constraints) {
        if (containsConstraint(constraints, ValidationConstraintCodes.SIZE)) {
            return Optional.of(ErrorCode.MBR_VAL_SIZE_NOTE);
        }
        return Optional.empty();
    }

    private boolean containsConstraint(String[] constraints, String expected) {
        if (constraints == null || constraints.length == 0) {
            return false;
        }
        return java.util.Arrays.stream(constraints)
                .filter(java.util.Objects::nonNull)
                .anyMatch(code -> code.equals(expected) || code.startsWith(expected + "."));
    }
}
