package com.zslin.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登陆用户DTO对象
 */
@Data
public class LoginUserDto implements Serializable {

    private Integer id;

    private String username;

    private String nickname;

    private String phone;

    public LoginUserDto() {
    }

    public LoginUserDto(Integer id, String username, String nickname, String phone) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.phone = phone;
    }
}
