package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 客户
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_customer")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String openid;

	private String unionid;

	/**
	* 昵称
	*/
	private String nickname;

	/**
	* 头像
	*/
	private String headImgUrl;

	/**
	* 关注状态
	* @remark 0-取消关注；1-关注
	*/
	private String status;

	/**
	* 关注日期
	*/
	private String followDay;

	/**
	* 关注时间
	*/
	private String followTime;

	/**
	* 关注时间Long类型
	*/
	private Long followLong;

	/**
	* 初次关注日期
	*/
	private String firstFollowDay;

	/**
	* 初次关注时间
	*/
	private String firstFollowTime;

	/**
	* 初次关注时间Long类型
	*/
	private Long firstFollowLong;

	/**
	* 手机号码
	*/
	private String phone;

	/**
	* 性别
	* @remark 1-男；2-女
	*/
	private String sex;

	/**
	* 性别
	*/
	private String name;

	/**
	* 代理ID
	*/
	private Integer agentId;

	/**
	* 微信绑定标记
	* @remark 0-未标记；1-已标记
	*/
	private String bindWx="0";

	/**
	* 上级CustomId
	*/
	private Integer leaderId;

	/**
	* 上级CustomNickname
	*/
	private String leaderNickname;

	/**
	* 上级CustomOpenid
	*/
	private String leaderOpenid;

	/**
	* 邀请者ID
	* @remark 邀请者CustomerId
	*/
	private Integer inviterId;

	/**
	* 邀请者Openid
	*/
	private String inviterOpenid;

	/**
	* 邀请者昵称
	*/
	private String inviterNickname;

}
