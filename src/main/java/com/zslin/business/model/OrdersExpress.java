package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 订单物流
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_orders_express")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdersExpress implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private Integer ordersId;

	private String ordersNo;

	private Integer customId;

	private String customNickname;

	private String openid;

	/**
	* 收货人信息
	*/
	private String addressCon;

	/**
	* 物流单号
	*/
	private String expNo;

	/**
	* 物流公司ID
	*/
	private Integer expId;

	/**
	* 物流公司名称
	*/
	private String expName;

	/**
	* 对应订单产品ID
	*/
	private Integer ordersProId;

	/**
	* 对应订单产品标题
	*/
	private String ordersProTitle;

	/**
	* 物流信息内容
	*/
	@Lob
	private String expCon;

	/**
	* 0：快递收件(揽件)1.在途中 2.正在派件 3.已签收 4.派送失败 5.疑难件 6.退件签收
	*/
	private String status;

	/**
	* 更新时间
	*/
	private String updateTime;

	/**
	* 更新时间，Long类型
	* @remark 方便控制重复查询
	*/
	private Long updateLong;

	private String createDay;

	private String createTime;

	private Long createLong;

}
