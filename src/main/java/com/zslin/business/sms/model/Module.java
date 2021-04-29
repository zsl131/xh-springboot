package com.zslin.business.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by 钟述林 393156105@qq.com on 2017/2/14 10:13.
 */
@Entity
@Table(name = "sms_module")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String status="0";

    private String sign;

    private String content;

    private String reason;

    /** 接口方ID */
    private Integer iid;
}
