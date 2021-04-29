package com.zslin.core.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求结果字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExplainResultField {

    /** 名称，如：错误信息 */
    String name() default "";

    /** 名称，如：message */
    String value() default "";

    /** 描述 */
    String notes() default "";

    String type() default "String";

}
