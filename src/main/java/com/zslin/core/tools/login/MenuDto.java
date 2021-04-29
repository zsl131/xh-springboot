package com.zslin.core.tools.login;

import com.zslin.core.model.AdminMenu;
import lombok.Data;

import java.util.List;

/**
 * Created by zsl on 2018/7/13.
 */
@Data
public class MenuDto {

    private AdminMenu menu;

    private List<AdminMenu> children;

    public MenuDto(AdminMenu menu, List<AdminMenu> children) {
        this.menu = menu;
        this.children = children;
    }
}
