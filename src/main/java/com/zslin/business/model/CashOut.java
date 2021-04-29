package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 提现申请
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_cash_out")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CashOut implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String createDay;

	private String createTime;

	private Long createLong;

	private Integer agentId;

	private String agentName;

	private String agentPhone;

	private String agentOpenid;

	/**
	* 批次号
	*/
	private String batchNo;

	/**
	* 记录条数
	*/
	private Integer amount;

	/**
	* 总金额
	*/
	private Float money;

	/**
	* 状态
	* @remark 0-待处理；1-已转款
	*/
	private String status="0";

	/**
	* 转款日期
	*/
	private String payDate;

	/**
	* 转款时间
	*/
	private String payTime;

	/**
	* 转款时间
	*/
	private Long payLong;

}
