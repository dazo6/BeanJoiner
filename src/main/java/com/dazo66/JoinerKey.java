package com.dazo66;

import java.lang.annotation.*;

/**
 * @author dazo
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JoinerKey {
    String[] value() default "";
}
