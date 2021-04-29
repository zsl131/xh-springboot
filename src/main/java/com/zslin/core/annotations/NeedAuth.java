package com.zslin.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否需要进行权限验证
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NeedAuth {

    //是否需要用户登陆，默认true
    boolean need() default true;

    //是否需要传入openid，默认false
    boolean openid() default false;
}
