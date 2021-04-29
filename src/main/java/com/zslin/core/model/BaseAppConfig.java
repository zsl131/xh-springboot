package com.zslin.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "base_app_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseAppConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String appName;

    /** 初始化标记 */
    private String initFlag;

    private String contact;

    private String address;

    private String phone;
}
