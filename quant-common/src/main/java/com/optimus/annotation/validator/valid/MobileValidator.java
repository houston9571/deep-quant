package com.optimus.annotation.validator.valid;

import cn.hutool.core.lang.Validator;
import com.optimus.annotation.validator.Mobile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MobileValidator implements ConstraintValidator<Mobile, String> {
    public MobileValidator() {
    }

    public void initialize(Mobile card) {
    }

    public boolean isValid(String value, ConstraintValidatorContext arg) {
        return value == null || Validator.isMobile(value);
    }
}
