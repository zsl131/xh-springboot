package com.zslin.core.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求参数
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExplainParam {

    /** 参数名称，如：用户名称 */
    String name() default "";

    /** 参数名称，如：username */
    String value();

    /** 是否必须 */
    boolean require() default false;

    /** 参数数据类型 */
    String type() default "String";

    /** 参数示例 */
    String example() default "";
}
