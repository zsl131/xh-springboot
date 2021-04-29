package com.zslin.core.tools.login;

import com.zslin.core.model.AdminMenu;
import lombok.Data;

import java.util.List;

/**
 * Created by zsl on 2018/7/13.
 */
@Data
public class MenuDtoDto {

    private AdminMenu menu;
    private List<MenuDto> children;

    public MenuDtoDto(AdminMenu menu, List<MenuDto> children) {
        this.menu = menu;
        this.children = children;
    }
}
