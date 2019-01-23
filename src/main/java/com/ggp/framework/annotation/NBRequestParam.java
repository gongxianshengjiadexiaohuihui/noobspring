package com.ggp.framework.annotation;

import java.lang.annotation.*;
/**
 * @Author:ggp
 * @Date:2019/1/21 16 04
 * @Description:
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NBRequestParam {
    String value() default "";
}
