package com.zslin.business.mini.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 影像墙
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "mini_image_wall")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageWall implements Serializable {

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
	* 关联标识
	* @remark 0-未关联；1-已关联
	*/
	private String relationFlag="0";

	/**
	* 关联产品ID
	*/
	private Integer relationProId;

	/**
	* 关联产品标题
	*/
	private String relationProTitle;

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

	/**
	* 文件类型
	* @remark 1-图片；2-视频
	*/
	private String fileType;

	/**
	* 文件地址
	*/
	private String url;

	/**
	* 评论次数
	*/
	private Integer commentCount=0;

	/**
	* 点赞次数
	*/
	private Integer goodCount=0;

	/**
	* 标题
	*/
	private String title;

	/**
	* 显示状态
	* @remark 0-隐藏（发布者可见）；1-显示
	*/
	private String status="0";

}
