package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 订单产品
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_orders_product")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdersProduct implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 消费者Openid
	*/
	private String openid;

	private String nickname;

	private Integer customId;

	/**
	* 消费者Unionid
	*/
	private String unionid;

	private Integer ordersId;

	private String ordersKey;

	/**
	* 订单编号
	*/
	private String ordersNo;

	/**
	* 是否存在售后
	* @remark 0-不存在；1-存在；默认0
	*/
	private String hasAfterSale="0";

	/**
	* 代理ID
	*/
	private Integer agentId;

	/**
	* 代理Openid
	*/
	private String agentOpenid;

	/**
	* 代理unionid
	*/
	private String agentUnionid;

	/**
	* 代理等级ID
	* @remark 对应代理当前等级
	*/
	private Integer agentLevelId;

	/**
	* 代理等级名称
	* @remark 对应代理当前等级
	*/
	private String agentLevelName;

	/**
	* 产品ID
	*/
	private Integer proId;

	/**
	* 产品照片
	*/
	private String proImg;

	/**
	* 产品标题
	*/
	private String proTitle;

	/**
	* 产品规格ID
	*/
	private Integer specsId;

	/**
	* 产品规格名称
	*/
	private String specsName;

	/**
	* 产品原价
	*/
	private Float oriPrice;

	/**
	* 产品单价
	*/
	private Float price;

	/**
	* 产品数量
	*/
	private Integer amount;

	/**
	* 基金金额
	* @remark 单个产品基金金额*amount
	*/
	private Float fund;

	/**
	* 产品销售类型
	* @remark 1-当季；2-预售
	*/
	private String saleMode="1";

	/**
	* 预计发货时间
	* @remark 当saleMode为2时，此值有效
	*/
	private String deliveryDate;

	private String backDay;

	private String backTime;

	private Long backLong;

	/**
	* 退款金额
	*/
	private Float backMoney=0f;

	private String payDay;

	private String payTime;

	private Long payLong;

	/**
	* 状态
	* @remark  -10:删除；-2:售后；-1：关闭；0-未付款；1-已付款，未发货；2-已发货；3-未点评；4-已完成
	*/
	private String status="0";

	/**
	* 自动抵扣佣金金额
	* @remark 当下单则就是代理时，直接抵扣佣金
	*/
	private Float autoCommissionMoney=0f;

}
