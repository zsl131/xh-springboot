package com.zslin.code.dto;

import lombok.Data;

/**
 * 字段DTO对象
 */
@Data
public class FieldDto {

    /** 名称 */
    private String name;

    /** 类型，如：String */
    private String type;

    /** 说明 */
    private String desc;

    /** 备注 */
    private String remark;

    /** 验证信息 */
    private String validations;
}
