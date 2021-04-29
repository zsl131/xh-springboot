package com.zslin.core.validate;

import lombok.Data;

import java.io.Serializable;

/**
 * Validation验证异常信息
 */
@Data
public class ObjectError implements Serializable {

    /** 对应的属性名称 */
    private String property;

    /** 对应的异常信息 */
    private String msg;

    public ObjectError(String property, String msg) {
        this.property = property;
        this.msg = msg;
    }
}
