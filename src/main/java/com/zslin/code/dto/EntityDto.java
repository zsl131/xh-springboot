package com.zslin.code.dto;

import lombok.Data;

import java.util.List;

/**
 * 对象DTO
 */
@Data
public class EntityDto {

    /** 包名 */
    private String pck;

    /** 类名 */
    private String cls;

    /** 描述 */
    private String desc;

    /** 作者 */
    private String author;

    /** 父模块名称 */
    private String pModuleName;

    /** 管理链接地址 */
    private String url;

    /** 生成功能CURDL */
    private String funs;

    /** 字段列表 */
    private List<FieldDto> fields;
}
