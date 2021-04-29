package com.zslin.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

/**
 * 产品信息
 *
 */
@Data
@Entity
@Table(name = "base_admin_menu")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 菜单的名称，中文显示名称
     */
    private String name;
    /**
     * 菜单的唯一英文标识，如:user,auth等
     */
    private String sn;

    /**
     * 菜单的顺序
     */
    private int orderNo;
    /**
     * 是否显示，0表示不显示，1表示显示
     */
    private int display;

    /**
     * 菜单的链接地址
     */
    private String href;
    /**
     * 上一级菜单
     */
    private Integer pid;

    private String pname;
    /**
     * 父类的sn
     */
    private String psn;

    /**
     * 菜单的图标
     */
    private String icon;

    /** 菜单类型，1：导航菜单；2：权限菜单 */
    private String type;
}
