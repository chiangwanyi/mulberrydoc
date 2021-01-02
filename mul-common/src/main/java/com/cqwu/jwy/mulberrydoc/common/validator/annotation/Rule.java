package com.cqwu.jwy.mulberrydoc.common.validator.annotation;

import java.lang.annotation.*;

/**
 * 校验规则
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Rule {
    String name();

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    boolean email() default false;

    boolean unique() default false;

    String regex() default "";
}
