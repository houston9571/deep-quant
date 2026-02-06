package com.optimus.annotation.validator.valid;

import cn.hutool.core.util.StrUtil;
import com.optimus.annotation.validator.ValueRange;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

@Slf4j
public class ValueRangeValidator implements ConstraintValidator<ValueRange, Object> {

    private double[] numberRange;
    private String[] stringRange;

    public ValueRangeValidator() {
    }

    @Override
    public void initialize(ValueRange valueRange) {
        numberRange = valueRange.numberRange();
        stringRange = valueRange.stringRange();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext arg) {
        if (value == null || StrUtil.isBlank(value.toString())) {
            return true;
        }
        try {
            if (value instanceof String) {
                return Arrays.asList(stringRange).contains(value.toString());
            }
            if (value instanceof Number) {
                return Arrays.stream(numberRange).anyMatch(d -> d == Double.parseDouble(value.toString()));
            }
        } catch (Exception e) {
            log.error("ValueRangeValidator.isValid ", e);
        }
        return false;
    }

}
