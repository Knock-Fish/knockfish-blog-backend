package com.knockfish.annotation.validation;

import com.knockfish.dto.user.UserUpdatePwdDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserUpdatePwdDTO> {
    @Override
    public boolean isValid(UserUpdatePwdDTO dto, ConstraintValidatorContext context) {
        return dto.getPassword() != null && dto.getPassword().equals(dto.getConfirmPassword());
    }
}
