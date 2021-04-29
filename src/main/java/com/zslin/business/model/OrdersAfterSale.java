package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 订单售后
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_orders_after_sale")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdersAfterSale implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 订单ID
	*/
	private Integer ordersId;

	/**
	* OrdersProduct的ID
	*/
	private Integer orderProId;

	/**
	* Product的ID
	*/
	private Integer proId;

	private String proTitle;

	private Integer specsId;

	private String specsName;

	/**
	* 订单编号
	*/
	private String ordersNo;

	private Float oriPrice;

	private Float price;

	private String unionid;

	/**
	* 联系人
	*/
	private String phone;

	/**
	* 处理内容
	*/
	@Lob
	private String content;

	/**
	* 照片信息
	*/
	@Lob
	private String imgs;

	/**
	* 是否退款
	* @remark 0-未退款；1-退款
	*/
	private String hasRefund="0";

	/**
	* 退款金额
	*/
	private Integer refundMoney=0;

	private String createDay;

	private String createTime;

	private Long createLong;

	private String endDay;

	private String endTime;

	private Long endLong;

	private String nickname;

	private String openid;

	private Integer customId;

	/**
	* 状态
	* @remark 0-处理中；1-处理完成
	*/
	private String status="0";

}
