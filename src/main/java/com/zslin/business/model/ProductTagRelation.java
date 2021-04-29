package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 产品标签关系
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_product_tag_relation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductTagRelation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 产品ID
	* @remark 外键
	*/
	private Integer proId;

	/**
	* 标签ID
	* @remark 外键
	*/
	private Integer tagId;

}
