package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 代理佣金明细
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_custom_commission_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomCommissionRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 代理ID
	*/
	private Integer agentId;

	private String agentOpenid;

	private String agentUnionid;

	private String agentName;

	private String agentPhone;

	/**
	* 当前等级ID
	*/
	private Integer agentLevelId;

	/**
	* 当前等级名称
	*/
	private String agentLevelName;

	/**
	* 销售员ID
	*/
	private Integer salerId;

	private String salerOpenid;

	private String salerName;

	private String salerPhone;

	/**
	* 规格ID
	*/
	private Integer specsId;

	/**
	* 规格名称
	*/
	private String specsName;

	/**
	* 件数
	*/
	private Integer specsCount=0;

	/**
	* 佣金金额
	*/
	private Float money;

	/**
	* 产品ID
	*/
	private Integer proId;

	/**
	* 产品标题
	*/
	private String proTitle;

	/**
	* 获得类型
	* @remark 0-自己推广；1-下级推广
	*/
	private String haveType;

	/**
	* 状态
	* @remark  -2：售后；-1：用户取消；0-用户下单；1-用户付款，但不在提现期；2-在提现期；3-纳入结算清单；4-结算到账；5-结算失败
	*/
	private String status;

	/**
	* 售后标记
	* @remark 0-无售后;1-有售后，可提现；2-有售后-不可提现
	*/
	private String saleFlag="0";

	private String createTime;

	private String createDay;

	/**
	* 格式yyyyMM
	*/
	private String createMonth;

	private Long createLong;

	/**
	* 对应用户ID
	*/
	private Integer customId;

	/**
	* 对应用户昵称
	*/
	private String customNickname;

	/**
	* 对应用户Openid
	*/
	private String customOpenid;

	/**
	* 对应用户Unionid
	*/
	private String customUnionid;

	/**
	* 订单编号
	*/
	private String ordersNo;

	/**
	* 订单ID
	*/
	private Integer ordersId;

	/**
	* 订单KEY
	*/
	private String ordersKey;

	/**
	* 提现的批次号
	*/
	private String cashOutBatchNo;

	/**
	* 发起提现日期
	*/
	private String cashOutDay;

	/**
	* 发起提现时间
	*/
	private String cashOutTime;

	/**
	* 发起提现时间
	*/
	private Long cashOutLong;

	/**
	* 提现支付日期
	*/
	private String payOutDay;

	/**
	* 提现支付时间
	*/
	private String payOutTime;

	/**
	* 提现支付时间
	*/
	private Long payOutLong;

	/**
	* 是否自动抵扣
	*/
	private String isAuto="0";

}
