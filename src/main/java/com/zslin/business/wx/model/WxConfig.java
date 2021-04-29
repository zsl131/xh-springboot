package com.zslin.business.wx.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 微信配置
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "wx_wx_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WxConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String url;

	private String appid;

	private String secret;

	private String aesKey;

	private String token;

	/**
	* 事件模板消息
	* @remark 默认模板消息
	*/
	private String eventTemp;

}
