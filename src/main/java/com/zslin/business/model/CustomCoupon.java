package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 客户优惠券
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_custom_coupon")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomCoupon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String openid;

	private String unionid;

	private Integer customId;

	private String nickname;

	/**
	* 优惠券ID
	*/
	private Integer couponId;

	/**
	* 优惠券名称
	*/
	private String couponName;

	/**
	* 优惠券说明
	*/
	private String remark;

	/**
	* 价值
	* @remark 优惠券的价值，单位元
	*/
	private Float worth;

	/**
	* 可否重复
	* @remark 0-不能重复使用；1-可重复使用
	*/
	private String canRepeat;

	/**
	* 有效截止时间
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String endTime;

	/**
	* 有效截止时间Long格式
	*/
	private Long endLong;

	/**
	* 状态
	* @remark 1-可使用；2-过期；3-已使用
	*/
	private String status="1";

	/**
	* 指定产品ID
	* @remark 0-全场通用；非0则指定产品使用
	*/
	private Integer proId;

	/**
	* 指定产品标题
	* @remark proId为0时，则“通用券”
	*/
	private String proTitle;

	/**
	* 领取日期
	* @remark 格式：yyyy-MM-dd
	*/
	private String createDay;

	/**
	* 领取时间
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String createTime;

	/**
	* 领取时间
	* @remark Long格式
	*/
	private Long createLong;

	/**
	* 规则SN，通过这个判断是否领取
	*/
	private String ruleSn;

	/**
	* 获取时的key
	* @remark 每一次获取都对应一个Key，为了防止重复领取
	*/
	private String receiveKey;

	/**
	* 是否查看
	*/
	private String hasRead="0";

	/**
	* 满减
	*/
	private Float reachMoney;

}
