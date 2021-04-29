package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 优惠券明细
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_coupon_rule_detail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponRuleDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 规则名称
	*/
	private String ruleName;

	/**
	* 规则ID
	*/
	private Integer ruleId;

	/**
	* 规则SN
	*/
	private String ruleSn;

	/**
	* 优惠券ID
	*/
	private Integer couponId;

	/**
	* 优惠券名称
	*/
	private String couponName;

}
