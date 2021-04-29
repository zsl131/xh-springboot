package com.zslin.business.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by 钟述林 393156105@qq.com on 2017/2/14 10:17.
 */
@Entity
@Table(name = "sms_send_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SendRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String phone;

    @Column(name = "module_id")
    private Integer moduleId;

    private String content;

    private String status;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "create_long")
    private Long createLong;

    @Column(name = "create_day")
    private String createDay;

    @Column(name = "create_time")
    private String createTime;

    public Date getCreateDate() {
        return createDate;
    }

    private String msg;

    /** 计数，该条短信算几条 */
    private Integer amount;
}
