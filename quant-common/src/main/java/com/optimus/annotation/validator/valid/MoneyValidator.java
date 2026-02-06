package com.optimus.annotation.validator.valid;

import com.optimus.annotation.validator.Money;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class MoneyValidator implements ConstraintValidator<Money, Object> {

    private String moneyRegex;

    private String negateRegex;

    private boolean negate;

    public MoneyValidator() {
    }

    @Override
    public void initialize(Money money) {
        this.negate = money.negate();
        this.moneyRegex = "^\\d+(\\.\\d{1," + money.decimal() + "})?$";
        this.negateRegex = "^-\\d+(\\.\\d{1," + money.decimal() + "})?$";
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext arg) {
        if (value == null) {
            return true;
        }
        if (negate) {
            return Pattern.compile(moneyRegex).matcher(value.toString()).matches() || Pattern.compile(negateRegex).matcher(value.toString()).matches();
        }
        return Pattern.compile(moneyRegex).matcher(value.toString()).matches();
    }

}
