package com.zslin.business.mini.dto;

import lombok.Data;

/**
 * 小程序端获取用户信息的DTO对象
 */
@Data
public class NewCustomDto {

    /** 县 */
    private String country;

    /** 授权代码，通过此代码获取openid */
    private String code;

    /** 性别 1-男；2-女 */
    private Integer gender;

    /** 省 */
    private String province;

    /** 市 */
    private String city;

    /** 头像地址 */
    private String avatarUrl;

    /** 昵称 */
    private String nickName;

    private String openId;

    private String unionId;
}
