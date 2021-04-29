package com.zslin.business.mini.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CustomMessageDto {
    private String touser;

    /** 模板ID */
    private String msgtype="text";


    /** 模板消息的内容 */
    private Map<String, String> text;
}
