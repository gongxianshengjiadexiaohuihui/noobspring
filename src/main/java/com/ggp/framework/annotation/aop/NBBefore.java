package com.ggp.framework.annotation.aop;

import java.lang.annotation.*;

/**
 * @Author:ggp
 * @Date:2019/1/26 14 46
 * @Description:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NBBefore {
    String value() default "";
}
