package com.zslin.business.finance.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

/**
 * 财务单据
 * Created by zsl on 2019/1/9.
 */
@Entity
@Table(name = "f_finance_ticket")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinanceTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "ticket_no")
    private String ticketNo;

    /** 对应的ID */
    @Column(name = "detail_id")
    private Integer detailId;

    @Column(name = "pic_url")
    private String picUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public Integer getDetailId() {
        return detailId;
    }

    public void setDetailId(Integer detailId) {
        this.detailId = detailId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
