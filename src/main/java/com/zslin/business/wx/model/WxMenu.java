package com.zslin.business.wx.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 微信菜单
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "wx_wx_menu")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WxMenu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 名称
	*/
	private String name;

	/**
	* 序号
	*/
	private Integer orderNo;

	private String url;

	private Integer pid;

	private String status="0";

	/**
	* 小程序appid
	*/
	private String appid;

	/**
	* 小程序路径
	*/
	private String pagePath;

	/**
	* 菜单类型
	* @remark view、click、miniprogram
	*/
	private String type;

	/**
	* 点击时的值
	*/
	private String optKey;

	/**
	* 微信素材ID
	*/
	private String mediaId;

}
