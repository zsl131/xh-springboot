package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 文章
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_topic")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Topic implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 用于标识文章唯一性
	*/
	private String sn;

	/**
	* 标题
	*/
	private String title;

	@Lob
	private String content;

	/**
	* 用于再次编辑的图文内容
	*/
	@Lob
	private String rawContent;

	/**
	* Json格式的图文内容
	*/
	@Lob
	private String jsonContent;

	private Integer readCount=0;

	private String createDay;

	private String createTime;

	private Long createLong;

	private String updateDay;

	private String updateTime;

	private Long updateLong;

}
