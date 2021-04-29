package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 留声机
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_phonograph")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Phonograph implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 创建日期
	* @remark 格式：yyyy-MM-dd
	*/
	private String createDay;

	/**
	* 创建时间
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String createTime;

	/**
	* 创建时间
	* @remark Long格式
	*/
	private Long createLong;

	/**
	* 订单编号
	*/
	private String ordersNo;

	/**
	* 密码
	* @remark 播放时需要输入该密码，可以是：无密码、收货人电话、自定义
	*/
	private String password;

	/**
	* 播放次数
	*/
	private Integer playRecord=0;

	/**
	* 留声人openid
	*/
	private String openid;

	private Integer customId;

	/**
	* 留声人nickname
	*/
	private String nickname;

	/**
	* 录音地址
	*/
	private String url;

	/**
	* 上传录音的token
	* @remark 用于获取具体的录音对象
	*/
	private String ticket;

}
