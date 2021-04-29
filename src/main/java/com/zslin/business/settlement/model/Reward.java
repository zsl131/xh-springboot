package com.zslin.business.settlement.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 提成奖励金
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "settlement_reward")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reward implements Serializable {

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
	* 产生的月份，如：yyyyMM
	* @remark 即因为哪个月销售高而获得的奖励
	*/
	private String produceMonth;

	private String produceYear;

	private String createDay;

	private String createTime;

	private Long createLong;

	/**
	* 总提成金额
	*/
	private Float commissionMoney=0f;

	/**
	* 额外奖励
	*/
	private Float extraMoney=0f;

	/**
	* 状态
	* @remark 0-未领取完；1-已领取完
	*/
	private String status;

	/**
	* 剩余领取次数
	* @remark 剩余为0也表示领取完成
	*/
	private Integer surplusTimes=0;

	/**
	* 剩余领取的金额
	*/
	private Float surplusMoney=0f;

	/**
	* 可领总次数
	*/
	private Integer totalTimes=0;

	/**
	* 已领取金额
	*/
	private Float receiptMoney=0f;

	/**
	* 已领取次数
	*/
	private Integer receiptTimes=0;

	/**
	* 奖励金排名
	*/
	private Integer orderNo=0;

}
