package com.zslin.business.mini.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 订阅消息
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "mini_subscribe_message")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscribeMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 模板ID
	*/
	private String tempId;

	/**
	* 名称
	* @remark 方便了解模板用意
	*/
	private String name;

	/**
	* sn
	* @remark 模板编号，好记，不能改，在程序中写死
	*/
	private String sn;

	/**
	* 消息内容
	* @remark 如：thing1_phrase2_date3_thing4_name5
	*/
	@Lob
	private String content;

	/**
	* 消息内容对应备注
	* @remark 如：审核项目_审核状态_申请时间_备注信息_申请人
	*/
	@Lob
	private String remark;

}
