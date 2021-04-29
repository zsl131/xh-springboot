package com.zslin.core.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在类上添加
 * 添加在有接口的Service类上
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Explain {

    /** 接口名称，如：用户管理 */
    String name() default "";

    /** 接口名称，如：userService */
    String value() default "";

    /** 接口对应的说明 */
    String notes() default "";
}
