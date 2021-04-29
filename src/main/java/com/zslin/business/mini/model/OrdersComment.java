package com.zslin.business.mini.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 订单点评
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "mini_orders_comment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdersComment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private Integer ordersId;

	private String ordersNo;

	private Integer customId;

	private String customNickname;

	private String openid;

	private String headImgUrl;

	/**
	* 点评内容
	*/
	@Lob
	private String content;

	private String createDay;

	private String createTime;

	private Long createLong;

	/**
	* 回复内容
	*/
	@Lob
	private String reply;

	private String replyDate;

	private String replyTime;

	private Long replyLong;

	/**
	* 显示状态，0-不显示；1-显示
	*/
	private String status;

	/**
	* 点评标记，0-未回复；1-已回复
	*/
	private String replyFlag="0";

}
