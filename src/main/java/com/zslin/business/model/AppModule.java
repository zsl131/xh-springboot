package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;


/**
 * 移动端首页大功能展示
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_app_module")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppModule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 序号
	*/
	private Integer orderNo;

	/**
	* 图标
	*/
	private String icon;

	/**
	* 名称
	*/
	@NotBlank(message="模块名称不能为空")
	private String txt;

	/**
	* 背景色
	* @remark 如：#34CD6D
	*/
	private String bgColor;

	/**
	* 说明
	* @remark 小字，几个字的说明
	*/
	private String smallTxt;

	/**
	* 跳转链接地址
	*/
	private String path;

	/**
	* 显示状态
	* @remark 0-隐藏；1-显示
	*/
	private String status;

	/**
	* 链接打开的模式
	* @remark 如：navigate、switchTab
	*/
	private String navMode;

}
