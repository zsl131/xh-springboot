package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 轮播图
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_carousel")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Carousel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 简要标题
	*/
	private String title;

	/**
	* 排序序号
	*/
	private Integer orderNo;

	/**
	* 图片链接地址
	* @remark 可能是在七牛上
	*/
	private String url;

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
	* 内容
	* @remark 可空，可描述，可链接
	*/
	private String content;

	/**
	* 链接打开的模式
	* @remark 如：navigate、switchTab
	*/
	private String navMode;

	/**
	* 图片上传的token
	* @remark 用于与Medium进行匹配
	*/
	private String token;

}
