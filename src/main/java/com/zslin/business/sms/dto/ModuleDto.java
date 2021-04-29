package com.zslin.business.sms.dto;

import lombok.Data;

/**
 * Created by 钟述林 393156105@qq.com on 2017/2/14 15:57.
 * 接口方提供的短信模板DTO对象
 */
@Data
public class ModuleDto {

    private Integer id;

    private String status;

    private String sign;

    private String content;
}
