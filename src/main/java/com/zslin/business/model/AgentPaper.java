package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 代理证件
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_agent_paper")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentPaper implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 代理ID
	*/
	private Integer agentId;

	/**
	* 代理姓名
	*/
	private String agentName;

	/**
	* 媒介ID
	*/
	private Integer mediumId;

	/**
	* 文件路径
	*/
	private String filePath;

	/**
	* 证件名称
	* @remark 如：身份证正面
	*/
	private String fileName;

}
