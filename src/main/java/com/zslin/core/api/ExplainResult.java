package com.zslin.core.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API接口请求返回数据
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExplainResult {

    /** 结果名称 */
    String name();

    /** 结果说明 */
    String notes() default "";
}
