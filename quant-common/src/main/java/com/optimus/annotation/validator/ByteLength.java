package com.optimus.annotation.validator;

import com.optimus.annotation.validator.valid.ByteLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {ByteLengthValidator.class}
)
public @interface ByteLength {

    int min() default 0;

    int max() default 2147483647;

    String message() default "长度不合法";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
