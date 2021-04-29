package com.zslin.core.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于加在方法上
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExplainOperation {

    /** 接口名称，如：添加用户 */
    String name() default "";

    /** 接口名称，如：addUser */
    String value() default "";

    /** 接口说明 */
    String notes() default "";

    ExplainParam [] params() default {};

    /** 数据返回 */
    ExplainReturn [] back() ;
}
