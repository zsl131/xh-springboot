package com.zslin.core.api.dto;

import lombok.Data;

@Data
public class ExplainParamDto {

    /** 参数名称，如：用户名称 */
    private String name;

    /** 参数名称，如：username */
    private String value;

    /** 是否必须 */
    private boolean require;

    /** 参数数据类型 */
    private String type;

    /** 参数示例 */
    private String example;
}
