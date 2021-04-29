package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;


/**
 * 客户收货地址
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_custom_address")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomAddress implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String openid;

	private String unionid;

	private Integer customId;

	private String nickname;

	/**
	* 省级代码
	*/
	private String provinceCode;

	/**
	* 省级名称
	*/
	private String provinceName;

	/**
	* 市级代码
	*/
	private String cityCode;

	/**
	* 市级名称
	*/
	private String cityName;

	/**
	* 县级代码
	*/
	private String countyCode;

	/**
	* 县级名称
	*/
	private String countyName;

	/**
	* 街道地址
	*/
	@NotBlank(message="街道不能为空")
@Length(min=4, message="街道至少4个字")
	private String street;

	/**
	* 小程序选择地址的序号
	* @remark 如：0-0-1，表示第一个省；第一个市；第二个县区
	*/
	private String addressIndex;

	/**
	* 姓名
	*/
	@NotBlank(message="联系人不能为空")
	private String name;

	/**
	* 联系电话
	*/
	@NotBlank(message="联系电话不能为空")
	private String phone;

	/**
	* 邮编
	*/
	private String postCode;

	/**
	* 是否默认
	* @remark 0-不是默认；1-默认
	*/
	private String isDefault;

	/**
	* 是否来自微信
	* @remark 0-不是；1-是
	*/
	private String fromWx="0";

}
