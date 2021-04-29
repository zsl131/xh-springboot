package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 产品收藏记录
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_product_favorite_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductFavoriteRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 用户ID
	*/
	private Integer customId;

	/**
	* 产品ID
	*/
	private Integer proId;

	/**
	* 产品标题
	*/
	private String proTitle;

	/**
	* 产品图片
	*/
	private String proImg;

	private String openid;

	private String unionid;

	private String nickname;

	/**
	* 创建时间Long
	*/
	private Long createLong;

	/**
	* 创建时间Time
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String createTime;

	/**
	* 创建时间Date
	* @remark 格式：yyyy-MM-dd
	*/
	private String createDay;

}
