package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 订单
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_orders")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Orders implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 订单编号
	*/
	private String ordersNo;

	private String openid;

	private String unionid;

	/**
	* 用户头像
	*/
	private String headImgUrl;

	/**
	* 用户昵称
	*/
	private String nickname;

	/**
	* 用户ID
	*/
	private Integer customId;

	/**
	* 订单内总产品数量
	*/
	private Integer totalCount;

	/**
	* 已发货数量
	*/
	private Integer sendCount=0;

	/**
	* 订单总金额
	*/
	private Float totalMoney;

	/**
	* 订单内产品件数
	*/
	private Integer specsCount=0;

	/**
	* 点评数量
	*/
	private Integer commentCount=0;

	/**
	* 订单总优惠金额
	*/
	private Float discountMoney=0f;

	/**
	* 优惠原因
	* @remark 优惠券名称
	*/
	private String discountReason;

	/**
	* 订单实付金额
	* @remark 不含运费
	*/
	private Float payMoney;

	/**
	* 订单总基金金额
	*/
	private Float fundMoney;

	/**
	* 运费金额
	*/
	private Float freight;

	/**
	* 订单日期
	* @remark 格式：yyyy-MM-dd
	*/
	private String createDay;

	/**
	* 订单时间
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String createTime;

	/**
	* 订单时间
	* @remark Long格式
	*/
	private Long createLong;

	/**
	* 付款日期
	*/
	private String payDay;

	/**
	* 付款时间
	*/
	private String payTime;

	/**
	* 付款时间Long类型
	*/
	private Long payLong;

	/**
	* 订单状态
	* @remark  -10:删除；-2:售后；-1：关闭；0-未付款；1-已付款，未发货；2-已发货；3-未点评；4-已完成
	*/
	private String status;

	/**
	* 售后标记
	* @remark 0-无售后;1-有售后
	*/
	private String saleFlag="0";

	/**
	* 退款金额
	*/
	private Float backMoney=0f;

	/**
	* 是否存在售后
	* @remark 0-不存在；1-存在；默认0
	*/
	private String hasAfterSale="0";

	/**
	* 是否有代理
	*/
	private String hasAgent="0";

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
	* 代理姓名
	*/
	private String agentName;

	/**
	* 代理电话
	*/
	private String agentPhone;

	/**
	* 是否默认代理
	* @remark 没有推荐者则使用默认的代理
	*/
	private String defaultAgent="0";

	/**
	* 佣金总额
	* @remark 各级代理所提佣金总额
	*/
	private Float totalCommission=0f;

	/**
	* 订单密钥
	* @remark 为客户端快速响应，增加key作为订单标识
	*/
	private String ordersKey;

	/**
	* 收货地址ID
	*/
	private Integer addressId;

	/**
	* 收货地址详情
	* @remark 姓名,地址,电话
	*/
	private String addressCon;

	/**
	* 备注信息
	*/
	@Lob
	private String remark;

	/**
	* 结束日期
	*/
	private String endDay;

	private String endTime;

	private Long endLong;

	/**
	* 发货日期
	*/
	private String sendDay;

	private String sendTime;

	private Long sendLong;

	/**
	* 订单中的产品标题，便于搜索
	*/
	@Lob
	private String proTitles;

	/**
	* 申请退款标记
	* @remark  -1：驳回申请；0-未申请；1-申请退款中；2-申请通过；
	*/
	private String refundFlag="0";

	/**
	* 退款申请结果
	*/
	private String refundVerifyReason;

	/**
	* 自动抵扣佣金金额
	* @remark 当下单则就是代理时，直接抵扣佣金
	*/
	private Float autoCommissionMoney=0f;

}
