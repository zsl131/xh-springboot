package com.zslin.business.wx.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 微信用户
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "wx_wx_account")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WxAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String openid;

	private String nickname;

	private String headImgUrl;

	/**
	* 关注状态
	* @remark 0-取消关注；1-关注
	*/
	private String status;

	private String createDay;

	private String createTime;

	private Long createLong;

	private String followDay;

	private String followTime;

	private Long followLong;

	/**
	* 用户类型
	* @remark 0-顾客；1-代理；5-公司员工；10-管理员
	*/
	private String type="0";

}
