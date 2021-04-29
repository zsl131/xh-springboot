package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 代理等级关系
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_agent_level_relation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentLevelRelation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 代理ID
	*/
	private Integer agentId;

	private String openid;

	private String unionid;

	/**
	* 等级ID
	*/
	private Integer levelId;

}
