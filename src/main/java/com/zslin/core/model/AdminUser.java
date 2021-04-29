package com.zslin.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "base_admin_user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String nickname;

    private String password;

    private String phone;

    private String sex;

    private String createDate;

    private String status = "1";

    /** 是否是管理员 */
    private String isAdmin = "0";
}
