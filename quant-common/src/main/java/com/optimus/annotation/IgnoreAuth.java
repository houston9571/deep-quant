package com.optimus.annotation;

import java.lang.annotation.*;

/**
 * 免验证token
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuth {

}
