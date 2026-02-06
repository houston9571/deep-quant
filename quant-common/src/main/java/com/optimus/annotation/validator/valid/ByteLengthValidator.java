package com.optimus.annotation.validator.valid;

import cn.hutool.core.util.StrUtil;
import com.optimus.annotation.validator.ByteLength;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class ByteLengthValidator implements ConstraintValidator<ByteLength, String> {

    private int min;

    private int max;

    public ByteLengthValidator() {

    }

    public void initialize(ByteLength byteLength) {
        min = byteLength.min();
        max = byteLength.max();
    }

    public boolean isValid(String value, ConstraintValidatorContext arg) {
        if (StrUtil.isBlank(value)){
            return true;
        }
        return StrUtil.bytes(value, "GBK").length >= min && StrUtil.bytes(value, "GBK").length <= max;
    }
}
