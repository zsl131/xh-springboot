package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 搜索记录
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_search_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 关键字
	* @remark 如：昭通 苹果
	*/
	private String keyword;

	/**
	* 搜索时间Long
	*/
	private Long createLong;

	/**
	* 搜索时间Time
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String createTime;

	/**
	* 搜索时间Date
	* @remark 格式：yyyy-MM-dd
	*/
	private String createDay;

	/**
	* 搜索次数
	*/
	private Integer count;

	/**
	* 用户对应的Openid
	*/
	private String openid;

	private String unionid;

	/**
	* 用户昵称
	*/
	private String nickname;

	private Integer customId;

	private String updateDay;

	private String updateTime;

	private Long updateLong;

}
