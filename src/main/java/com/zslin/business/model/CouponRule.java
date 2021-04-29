package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 优惠券规则
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_coupon_rule")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponRule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 名称
	*/
	private String name;

	/**
	* 优惠券ID
	*/
	private Integer couponId;

	/**
	* 优惠券名称
	*/
	private String couponName;

	/**
	* 规则SN，唯一，不可修改
	*/
	private String ruleSn;

}
