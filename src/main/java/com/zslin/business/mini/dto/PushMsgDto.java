package com.zslin.business.mini.dto;

import lombok.Data;

import java.util.Map;

/**
 * 小程序订阅消息DTO对象
 */
@Data
public class PushMsgDto {

    private String touser;

    /** 模板ID */
    private String template_id;

    /** 点击时跳转的页面 */
    private String page = "";

    /** 模板消息的内容 */
    private Map<String, SingleDataDto> data;
}
