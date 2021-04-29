package com.zslin.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 小程序传入参数
 * - 用户DTO对象
 */
@Data
public class WxCustomDto implements Serializable {

    private Integer customId;

    private String openid;

    private String unionid;

    private String nickname;

    private String headImgUrl;
}
