package com.zslin.business.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by zsl on 2018/9/25.
 */
@Entity
@Table(name = "sms_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SmsConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    /** 发送短信的URL地址 */
    private String url;

    /** 身份认证信息 */
    private String token;

    /** 添加模板的Code */
    @Column(name = "add_module")
    private String addModule;

    /** 删除模板的Code */
    @Column(name = "del_module")
    private String delModule;

    /** 列表模板的Code */
    @Column(name = "list_module")
    private String listModule;

    /** 查询短信余额的Code */
    private String surplus;

    /** 发送短信的Code */
    @Column(name = "send_msg")
    private String sendMsg;

    /** 发送短信的接口ID */
    @Column(name = "send_code_iid")
    private Integer sendCodeIId;
}
