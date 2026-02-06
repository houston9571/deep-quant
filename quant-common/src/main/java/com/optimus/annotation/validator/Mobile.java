package com.optimus.annotation.validator;

import com.optimus.annotation.validator.valid.MobileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {MobileValidator.class}
)
public @interface Mobile {

    String message() default "身份证信息不合法";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
