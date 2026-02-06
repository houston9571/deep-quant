package com.optimus.annotation;


import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.text.StrPool.COLON;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RequestLimit {

    /**
     * redis 锁key的前缀
     * @return redis 锁key的前缀
     */
    String prefix() default "";

    /**
     * 过期分钟数,默认为1分钟
     * @return 轮询锁的时间
     */
    int expire() default 1;

    /**
     * 超时时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

    /**
     * 限制时间内，允许访问的次数
     * @return 次数
     */
    int limit() default 1;

    /**
     * key分隔符号
     * @return
     */
    String delimiter() default COLON;

}
