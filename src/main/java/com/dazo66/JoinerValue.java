package com.dazo66;

import java.lang.annotation.*;

/**
 * @author dazo
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JoinerValue {
    String value() default "";
    boolean isGroup() default false;
}
