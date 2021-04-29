package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 素材
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_medium")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Medium implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 归属对象ID
	*/
	private Integer objId;

	/**
	* 归属对象名称
	*/
	private String objClassName;

	/**
	* 素材类型，1-图片；2-视频
	*/
	private String type;

	/**
	* 文件大小
	*/
	private Long fileSize;

	/**
	* 序号，用于在单个产品内进行多张图片排序显示
	*/
	private Integer orderNo;

	/**
	* 用于临时上传还未确定归属对象
	*/
	private String ticket;

	/**
	* 上传完成的时间Long
	*/
	private Long createLong;

	/**
	* 是否为封面，1-是；0-否
	*/
	private String isFirst;

	/**
	* 七牛服务器上的key
	*/
	private String qiniuKey;

	/**
	* 状态
	*/
	private String status="1";

	/**
	* 根链接，配合qiniuKey一起使用
	*/
	private String rootUrl;

}
