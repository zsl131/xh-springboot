package com.zslin.business.mini.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 影像评论
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "mini_image_comment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageComment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 创建日期
	* @remark 格式：yyyy-MM-dd
	*/
	private String createDay;

	/**
	* 创建时间
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String createTime;

	/**
	* 创建时间
	* @remark Long格式
	*/
	private Long createLong;

	/**
	* 评论内容
	*/
	@Lob
	private String content;

	/**
	* 显示状态
	* @remark 0-隐藏（评论者可见）；1-显示
	*/
	private String status="0";

	/**
	* 点赞次数
	*/
	private Integer goodCount=0;

	/**
	* 用户ID
	*/
	private Integer customId;

	private String customOpenid;

	private String customNickname;

	private String customUnionid;

	/**
	* 用户头像
	*/
	private String headImgUrl;

}
