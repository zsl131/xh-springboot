package com.zslin.business.settlement.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 提成奖励金规则
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "settlement_reward_rule")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RewardRule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 状态，是否启用
	* @remark 0-不启用；1-启用
	*/
	private String status;

	/**
	* 起点金额，即要达到这个金额的提成才能获得奖励金
	*/
	private Float startMoney=0f;

	/**
	* 奖金比例，如0.6即提成金额的60%
	*/
	private Float rewardRate;

	/**
	* 每月获取比例，即每月可领取奖金的比例
	*/
	private Float monthRate;

}
