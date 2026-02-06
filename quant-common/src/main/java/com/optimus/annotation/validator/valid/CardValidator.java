package com.optimus.annotation.validator.valid;

import cn.hutool.core.util.IdcardUtil;
import com.optimus.annotation.validator.Card;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CardValidator implements ConstraintValidator<Card, String> {

    public CardValidator() {

    }

    public void initialize(Card card) {
    }

    public boolean isValid(String value, ConstraintValidatorContext arg) {
        return value == null || IdcardUtil.isValidCard(value);
    }
}
