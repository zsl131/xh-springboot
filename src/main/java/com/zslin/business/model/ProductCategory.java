package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;


/**
 * 产品分类
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_product_category")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String sn;

	/**
	* 名称
	* @remark 如：苹果
	*/
	@NotBlank(message="分类名称不能为空")
	private String name;

	/**
	* 父id
	*/
	private Integer pid;

	/**
	* 排序序号
	*/
	private Integer orderNo;

	/**
	* 父名称
	* @remark 如：水果
	*/
	private String pname;

}
