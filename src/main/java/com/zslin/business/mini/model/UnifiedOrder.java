package com.zslin.business.mini.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 统一订单
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "mini_unified_order")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnifiedOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String openid;

	private Integer customId;

	private String nickname;

	private String headImgUrl;

	/**
	* 支付ID
	*/
	private String prepayId;

	/**
	* 状态
	* @remark 0-未支付；1-支付成功；2-支付失败
	*/
	private String status="0";

	/**
	* 订单编号
	*/
	private String ordersNo;

	/**
	* 订单ID
	*/
	private Integer ordersId;

	/**
	* 微信返回的err_code
	* @remark 错误代码
	*/
	private String errCode;

	/**
	* 微信返回的err_code_des
	* @remark 错误描述
	*/
	private String errCodeDes;

	private String createDay;

	private String createTime;

	private Long createLong;

	/**
	* 支付金额
	*/
	private Float payMoney;

}
