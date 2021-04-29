package com.zslin.business.mini.dto;

import lombok.Data;

/** 模板消息DTO对象 */
@Data
public class MsgDto {

    private String key;

    private String value;

    public MsgDto(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
