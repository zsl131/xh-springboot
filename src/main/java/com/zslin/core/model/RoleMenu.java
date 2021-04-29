package com.zslin.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by zsl-pc on 2016/9/1.
 */
@Entity
@Table(name="base_role_menu")
@JsonInclude(value= JsonInclude.Include.NON_NULL)
@Data
public class RoleMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 角色Id */
    private Integer rid;

    /** 菜单Id */
    private Integer mid;
}
