package com.ggp.framework.annotation.mvc;

import java.lang.annotation.*;
/**
 * @Author:ggp
 * @Date:2019/1/21 16 04
 * @Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NBController {
    String value() default "";
}
