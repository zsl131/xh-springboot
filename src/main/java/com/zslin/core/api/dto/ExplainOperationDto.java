package com.zslin.core.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExplainOperationDto {

    /** 接口名称，如：添加用户 */
    private String name;

    /** 接口名称，如：addUser */
    private String value;

    /** 接口说明 */
    private String notes;

    private List<ExplainParamDto> paramList;

    private List<ExplainReturnDto> returnList;
}
