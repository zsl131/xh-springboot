package com.zslin.business.finance.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

/**
 * 财务管理
 * Created by zsl on 2019/1/2.
 */
@Entity
@Table(name = "f_finance_detail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinanceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "create_date")
    private String createDate;

    @Column(name = "create_time")
    private String createTime;

    @Column(name = "create_long")
    private Long createLong;

    @Column(name = "record_date")
    private String recordDate;

    @Column(name = "record_year")
    private String recordYear;

    @Column(name = "record_month")
    private String recordMonth;

    /** 进出账标记，1-进账；-1-出账 */
    private String flag;

    private Float price;

    private Integer count = 1;

    /** 金额 */
    private Float amount = 0f;

    /** 账目标题 */
    private String title;

    /** 记账人姓名 */
    @Column(name = "record_name")
    private String recordName;

    /** 记账人电话 */
    @Column(name = "record_phone")
    private String recordPhone;

    /** 状态，1-正常；-1-作废 */
    private String status ="1";

    /** 作废原因 */
    @Column(name = "invalid_reason")
    private String invalidReason;

    /** 申请人，即登陆操作人的信息 */
    private String operator;

    /** 设置为作废的人 */
    @Column(name = "invalid_name")
    private String invalidName;

    /** 设置为作废的人的电话 */
    @Column(name = "invalid_phone")
    private String invalidPhone;

    @Column(name = "ticket_no")
    private String ticketNo;

    /** 备注 */
    private String remark;

    @Column(name = "cate_id")
    private Integer cateId;

    @Column(name = "cate_name")
    private String cateName;

    /** TicketNo */
    private Integer tno;

    @Column(name = "record_id")
    private Integer recordId;

    /** 应有单据张数 */
    @Column(name = "ticket_count")
    private Integer ticketCount = 0;

    /** 单据上传张数 */
    @Column(name = "ticket_upload_count")
    private Integer ticketUploadCount = 0;

    /** 经办人 */
    @Column(name = "handle_name")
    private String handleName;

    public String getHandleName() {
        return handleName;
    }

    public void setHandleName(String handleName) {
        this.handleName = handleName;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(Integer ticketCount) {
        this.ticketCount = ticketCount;
    }

    public Integer getTicketUploadCount() {
        return ticketUploadCount;
    }

    public void setTicketUploadCount(Integer ticketUploadCount) {
        this.ticketUploadCount = ticketUploadCount;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getTno() {
        return tno;
    }

    public void setTno(Integer tno) {
        this.tno = tno;
    }

    public Integer getCateId() {
        return cateId;
    }

    public void setCateId(Integer cateId) {
        this.cateId = cateId;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getInvalidName() {
        return invalidName;
    }

    public void setInvalidName(String invalidName) {
        this.invalidName = invalidName;
    }

    public String getInvalidPhone() {
        return invalidPhone;
    }

    public void setInvalidPhone(String invalidPhone) {
        this.invalidPhone = invalidPhone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getCreateLong() {
        return createLong;
    }

    public void setCreateLong(Long createLong) {
        this.createLong = createLong;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getRecordYear() {
        return recordYear;
    }

    public void setRecordYear(String recordYear) {
        this.recordYear = recordYear;
    }

    public String getRecordMonth() {
        return recordMonth;
    }

    public void setRecordMonth(String recordMonth) {
        this.recordMonth = recordMonth;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getRecordPhone() {
        return recordPhone;
    }

    public void setRecordPhone(String recordPhone) {
        this.recordPhone = recordPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInvalidReason() {
        return invalidReason;
    }

    public void setInvalidReason(String invalidReason) {
        this.invalidReason = invalidReason;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
