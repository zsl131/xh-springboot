package com.zslin.core.tools.login;

import com.zslin.core.model.AdminMenu;
import lombok.Data;

import java.util.List;

/**
 * Created by zsl on 2018/7/15.
 */
@Data
public class MenuTreeDto {

    private AdminMenu menu;

    private List<AdminMenu> children;

    public MenuTreeDto() {
    }

    public MenuTreeDto(AdminMenu menu, List<AdminMenu> children) {
        this.menu = menu;
        this.children = children;
    }
}
