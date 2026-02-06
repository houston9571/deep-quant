package com.optimus.annotation.validator.valid;

import cn.hutool.core.util.StrUtil;
import com.optimus.annotation.validator.EnumRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static cn.hutool.core.text.StrPool.COMMA;


@Slf4j
public class EnumRangeValidator implements ConstraintValidator<EnumRange, Object> {

    private Class<?> rangeClass;

    private String property;

    public EnumRangeValidator() {
    }

    public void initialize(EnumRange enumRange) {
        rangeClass = enumRange.rangeClass();
        property = enumRange.property();
    }

    public boolean isValid(Object value, ConstraintValidatorContext arg) {
        if (value == null || StrUtil.isBlank(value.toString())) {
            return true;
        }
        try {
            Object o = ReflectionUtils.invokeMethod(rangeClass.getMethod("ranges"), rangeClass);
            if (o instanceof List) {
                List list = (List) o;
                if(StrUtil.isNotBlank(property)) {
                    arg.disableDefaultConstraintViolation();
                    arg.buildConstraintViolationWithTemplate(property + " Value Range: " + StrUtil.join(COMMA, list)).addConstraintViolation();
                }
                if (value instanceof String) {
                    return list.contains(value.toString());
                } else if (value instanceof Number) {
                    return list.stream().anyMatch(d -> d.toString().equals(value.toString()));
                }
            }
        } catch (Exception e) {
            log.error("ValueRangeValidator.isValid ", e);
        }
        return false;
    }
}
