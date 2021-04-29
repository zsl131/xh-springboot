package com.zslin.business.mini.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 用户影像关联
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "mini_custom_image_relation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomImageRelation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 用户ID
	*/
	private Integer customId;

	private String customOpenid;

	private String customNickname;

	private String customUnionid;

	/**
	* 关系类型
	* @remark 1-可上传，但需要审核；2-可上传，无需审核
	*/
	private String type;

}
