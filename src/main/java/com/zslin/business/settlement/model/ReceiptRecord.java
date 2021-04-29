package com.zslin.business.settlement.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 奖金领取记录
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "settlement_receipt_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiptRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private Integer agentId;

	private String agentName;

	private String agentPhone;

	private Integer customId;

	private String customNickname;

	private String customOpenid;

	/**
	* 对应的奖金ID
	*/
	private Integer rewardId;

	private String rewardProduceMonth;

	private String rewardProduceYear;

	/**
	* 领取金额
	*/
	private Float money=0f;

	/**
	* 状态
	* @remark 0-提起领取；1-已转款
	*/
	private String status;

	private String createDay;

	/**
	* 创建月份，格式yyyyMM
	*/
	private String createMonth;

	private String createTime;

	private Long createLong;

	private String payDay;

	private String payTime;

	private Long payLong;

	/**
	* 次别
	*/
	private Integer times;

}
