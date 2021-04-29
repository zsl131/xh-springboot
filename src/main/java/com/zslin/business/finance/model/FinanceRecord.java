package com.zslin.business.finance.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

/**
 * Created by zsl on 2019/1/8.
 */
@Entity
@Table(name = "f_finance_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinanceRecord {

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

    /** 金额 */
    private Float amount = 0f;

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

    /** 经办人 */
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

    /** TicketNo */
    private Integer tno;

    /** 审核人 */
    @Column(name = "verify_name")
    private String verifyName;

    @Column(name = "verify_time")
    private String verifyTime;

    @Column(name = "detail_count")
    private Integer detailCount ;

    @Column(name = "ticket_count")
    private Integer ticketCount;

    public Integer getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(Integer ticketCount) {
        this.ticketCount = ticketCount;
    }

    public String getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }

    public Integer getDetailCount() {
        return detailCount;
    }

    public void setDetailCount(Integer detailCount) {
        this.detailCount = detailCount;
    }

    public String getVerifyName() {
        return verifyName;
    }

    public void setVerifyName(String verifyName) {
        this.verifyName = verifyName;
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

    public Integer getTno() {
        return tno;
    }

    public void setTno(Integer tno) {
        this.tno = tno;
    }
}
