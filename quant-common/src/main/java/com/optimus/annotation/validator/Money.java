package com.optimus.annotation.validator;

import com.optimus.annotation.validator.valid.MoneyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {MoneyValidator.class}
)
public @interface Money {

    boolean negate() default false;

    int decimal() default 4;

    String message() default "金额为正数，支持两位小数";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
