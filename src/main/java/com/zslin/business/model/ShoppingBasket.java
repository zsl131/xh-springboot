package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 购物车
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_shopping_basket")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingBasket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
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

	/**
	* 产品规格ID
	*/
	private Integer specsId;

	/**
	* 产品规格名称
	*/
	private String specsName;

	/**
	* 数量
	*/
	private Integer amount;

	/**
	* 价格，单条记录总价
	*/
	private Float price;

	private Float oriPrice;

	/**
	* 加入购物车日期
	* @remark 格式：yyyy-MM-dd
	*/
	private String createDay;

	/**
	* 加入购物车时间
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String createTime;

	/**
	* 加入购物车时间
	* @remark Long格式
	*/
	private Long createLong;

	/**
	* 修改时间
	* @remark 格式：yyyy-MM-dd
	*/
	private String updateDay;

	/**
	* 修改时间
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String updateTime;

	/**
	* 修改时间
	* @remark Long格式
	*/
	private Long updateLong;

	private String openid;

	private String unionid;

	private String nickname;

	private Integer customId;

}
