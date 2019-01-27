package com.ggp.framework.annotation.aop;

import java.lang.annotation.*;

/**
 * @Author:ggp
 * @Date:2019/1/26 14 51
 * @Description:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NBAfterReturn {
    String value() default "";
}
