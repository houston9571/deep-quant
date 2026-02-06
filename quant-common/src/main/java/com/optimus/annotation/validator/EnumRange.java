package com.optimus.annotation.validator;

import com.optimus.annotation.validator.valid.EnumRangeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EnumRangeValidator.class})
public @interface EnumRange {

    String message() default "值不在范围之内";

    String property() default "";

    Class<?> rangeClass();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
