package com.zslin.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by zsl-pc on 2016/9/1.
 */
@Entity
@Table(name="base_user_role")
@JsonInclude(value= JsonInclude.Include.NON_NULL)
@Data
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 用户Id */
    private Integer uid;

    /** 角色Id */
    private Integer rid;
}
