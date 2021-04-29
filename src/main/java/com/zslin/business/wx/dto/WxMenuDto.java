package com.zslin.business.wx.dto;

import com.zslin.business.wx.model.WxMenu;
import lombok.Data;

import java.util.List;

/**
 * 微信菜单DTO对象
 */
@Data
public class WxMenuDto {

    private WxMenu menu;

    private List<WxMenu> children;

    public WxMenuDto(WxMenu menu, List<WxMenu> children) {
        this.menu = menu;
        this.children = children;
    }
}
