package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;


/**
 * 优惠券
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_coupon")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coupon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 名称
	*/
	@NotBlank(message="名称不能为空")
	private String name;

	/**
	* 说明
	*/
	private String remark;

	/**
	* 价值
	* @remark 一张优惠券抵价多少钱，单位元
	*/
	private Float worth;

	/**
	* 可否重复
	* @remark 0-不能重复使用；1-可重复使用
	*/
	private String canRepeat;

	/**
	* 可获取状态
	* @remark 0-不可获取；1-可以获取
	*/
	private String status;

	/**
	* 有效时长
	* @remark 优惠券的有效时长，单位秒，0表示长期有效
	*/
	private Integer duration;

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
	* 满减
	* @remark 达到某值可以使用，0表示均可使用，单位元
	*/
	private Float reachMoney;

	/**
	* 剩余数量
	*/
	private Integer surplusCount=0;

	/**
	* 已领取数量
	*/
	private Integer receiveCount=0;

}
