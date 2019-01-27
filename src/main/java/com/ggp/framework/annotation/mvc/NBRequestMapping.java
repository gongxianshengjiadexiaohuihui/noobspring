package com.ggp.framework.annotation.mvc;

import java.lang.annotation.*;
/**
 * @Author:ggp
 * @Date:2019/1/21 16 04
 * @Description:
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NBRequestMapping {
    String value() default "";
}
