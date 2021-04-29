package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 订单优惠券
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_orders_coupon")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdersCoupon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 用户优惠券对应ID
	*/
	private Integer customCouponId;

	/**
	* 优惠券ID
	*/
	private Integer couponId;

	/**
	* 优惠券名称
	*/
	private String couponName;

	private String openid;

	private String unionid;

	/**
	* 订单ID
	*/
	private Integer ordersId;

	/**
	* 订单编号
	*/
	private String ordersNo;

	private String ordersKey;

	/**
	* 使用日期
	* @remark 格式：yyyy-MM-dd
	*/
	private String usedDay;

	/**
	* 使用时间
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String usedTime;

	/**
	* 使用时间
	* @remark Long格式
	*/
	private Long usedLong;

	/**
	* 抵价金额
	* @remark 单位元
	*/
	private Float discountMoney;

}
