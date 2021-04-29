package com.zslin.business.wx.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 微信小程序关联
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "wx_wx_mini")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WxMini implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String nickname;

	/**
	* 小程序用户ID
	*/
	private Integer customId;

	/**
	* 微信用户ID
	*/
	private Integer accountId;

	/**
	* 微信用户Openid
	*/
	private String wxOpenid;

	/**
	* 小程序用户Openid
	*/
	private String miniOpenid;

	private String createDay;

	private String createTime;

	private Long createLong;

}
