package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;


/**
 * 产品标签
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_product_tag")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductTag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 标签名称
	*/
	@NotBlank(message="标签名称不能为空")
	private String name;

	private Integer orderNo;

	/**
	* 显示状态
	*/
	private String status;

	/**
	* 对应产品ID
	*/
	private Integer proId;

	/**
	* 对应产品标题
	*/
	private String proTitle;

}
