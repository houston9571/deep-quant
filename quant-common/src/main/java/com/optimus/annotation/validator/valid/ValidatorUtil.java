package com.optimus.annotation.validator.valid;

import com.optimus.exception.ParamValidatorException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

/**
 * 参数验证工具
 **/
public class ValidatorUtil {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 按照验证组验证参数
     * @param object
     * @param groups
     * @throws ParamValidatorException
     */
    public static void validateEntity(Object object, Class<?>... groups) throws ParamValidatorException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            Iterator var4 = constraintViolations.iterator();
            if (var4.hasNext()) {
                ConstraintViolation<Object> constraint = (ConstraintViolation)var4.next();
                msg.append(constraint.getMessage());
            }
            throw new ParamValidatorException(msg.toString());
        }
    }

}
