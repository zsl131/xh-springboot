package com.zslin.core.annotations;

import java.lang.annotation.*;

/**
 * 进行
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheLock {
}
