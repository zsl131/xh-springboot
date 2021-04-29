package com.zslin.core.tools.login;

import com.zslin.core.model.AdminMenu;
import com.zslin.core.model.AdminUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsl on 2018/7/13.
 */
@Data
public class LoginDto {

    private AdminUser user;

    private List<MenuDto> navMenus;

    private List<AdminMenu> authMenus;

    //身份token
    private String token;

    public LoginDto() {
        navMenus = new ArrayList<>();
        authMenus = new ArrayList<>();
    }

    public LoginDto(List<MenuDto> navMenus, List<AdminMenu> authMenus) {
        this.navMenus = navMenus;
        this.authMenus = authMenus;
    }
}
