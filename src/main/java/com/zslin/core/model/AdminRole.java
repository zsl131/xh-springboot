package com.zslin.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "base_admin_role")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 角色名称 */
    private String name;

    /** 角色描述 */
    private String remark;

    private String sn;
}
