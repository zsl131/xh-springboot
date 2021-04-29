package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;


/**
 * 产品评论
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_product_comment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductComment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 产品ID
	* @remark 外键
	*/
	private Integer proId;

	/**
	* 产品名称
	* @remark 外键
	*/
	private String proTitle;

	/**
	* 显示状态
	* @remark 0-隐藏（评论者可见）；1-显示
	*/
	private String status;

	/**
	* 评论者openid
	*/
	private String openid;

	private String unionid;

	private Integer customId;

	/**
	* 评论者昵称
	*/
	private String nickname;

	/**
	* 评论日期
	* @remark 格式：yyyy-MM-dd
	*/
	private String commentDate;

	/**
	* 评论时间
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String commentTime;

	/**
	* 评论毫秒
	*/
	private Long commentLong;

	/**
	* 评论分值
	* @remark 1-5分
	*/
	private Integer commentScore;

	/**
	* 评论内容
	*/
	@Lob
	@NotBlank(message="评论内容不能为空")
	private String commentContent;

	/**
	* 回复日期
	* @remark 格式：yyyy-MM-dd
	*/
	private String replyDate;

	/**
	* 回复时间
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String replyTime;

	/**
	* 回复毫秒
	*/
	private Long replyLong;

	/**
	* 回复内容
	*/
	@Lob
	private String replyContent;

	/**
	* 回复人员信息
	*/
	private String replyOperator;

	/**
	* 对应的订单编号
	* @remark 与Orders对应
	*/
	private String ordersNo;

	/**
	* 对应订单产品ID
	* @remark 与OrdersProduct对应
	*/
	private Integer ordersProId;

	/**
	* 是否默认好评
	* @remark OrdersProduct在一段时间后自动默认好评
	*/
	private String isAuto;

}
