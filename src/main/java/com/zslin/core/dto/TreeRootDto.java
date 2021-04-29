package com.zslin.core.dto;

import com.zslin.core.model.AdminMenu;
import com.zslin.core.tools.login.MenuTreeDto;
import lombok.Data;

import java.util.List;

/**
 * Created by zsl on 2018/7/17.
 */
@Data
public class TreeRootDto {

    private List<MenuTreeDto> treeList;

    private List<AdminMenu> menuList;

    public TreeRootDto() {
    }

    public TreeRootDto(List<MenuTreeDto> treeList, List<AdminMenu> menuList) {
        this.treeList = treeList;
        this.menuList = menuList;
    }
}
