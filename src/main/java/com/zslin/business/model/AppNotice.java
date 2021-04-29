package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;


/**
 * 通知公告
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_app_notice")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppNotice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 显示内容
	*/
	@NotBlank(message="公告内容不能为空")
	private String content;

	/**
	* 显示状态
	* @remark 0-隐藏；1-显示
	*/
	private String status;

	/**
	* 打开方式
	* @remark 0-不打开；1-弹窗，显示content;2-打开链接
	*/
	private String openMode;

	/**
	* 链接打开的模式
	* @remark 如：navigate、switchTab
	*/
	private String navMode;

	/**
	* 跳转链接地址
	*/
	private String url;

}
