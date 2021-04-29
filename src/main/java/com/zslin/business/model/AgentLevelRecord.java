package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 代理等级变化记录
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_agent_level_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentLevelRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 代理ID
	*/
	private Integer agentId;

	/**
	* 变化前等级ID
	*/
	private Integer beforeLevelId;

	/**
	* 变化前等级名称
	*/
	private String beforeLevelName;

	/**
	* 变化后等级ID
	*/
	private Integer curLevelId;

	/**
	* 变化后等级名称
	*/
	private String curLevelName;

	/**
	* 变化标识
	* @remark 1-升级；2-降级
	*/
	private Integer flag;

	/**
	* 变化日期
	* @remark 格式：yyyy-MM-dd
	*/
	private String createDay;

	/**
	* 变化时间
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String createTime;

	/**
	* 变化时间
	* @remark Long格式
	*/
	private Long createLong;

	/**
	* 变化原因
	*/
	private String reason;

}
