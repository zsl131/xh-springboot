package com.zslin.business.mini.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;


/**
 * 小程序配置
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "mini_mini_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MiniConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 小程序Appid
	*/
	@NotBlank(message="appid不能为空")
	private String appid;

	/**
	* 商户号id
	* @remark 微信支付
	*/
	private String mchid;

	/**
	* Api密钥
	* @remark 微信支付
	*/
	private String apiKey;

	/**
	* 支付结果通知地址
	* @remark 微信支付
	*/
	private String payNotifyUrl;

	/**
	* 小程序密钥
	*/
	@NotBlank(message="appSecret不能为空")
	private String appSecret;

	/**
	* 请求地址
	*/
	private String requestUrl;

	/**
	* 上传地址
	*/
	private String uploadUrl;

	/**
	* 下载地址
	*/
	private String downloadUrl;

	/**
	* 消息推送地址
	*/
	private String msgUrl;

	/**
	* 消息推送Token
	*/
	private String msgToken;

	/**
	* 消息推送AESKey
	*/
	private String msgAesKey;

}
