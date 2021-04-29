package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 产品头像图
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_product_head_img")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductHeadImg implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 产品ID
	* @remark 外键
	*/
	private Integer proId;

	/**
	* 图片链接地址
	*/
	private String url;

	/**
	* 状态
	*/
	private String status="1";

	/**
	* 排序序号
	*/
	private Integer orderNo;

}
