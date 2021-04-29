package com.zslin.business.agent.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 代理角色关联
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "agent_agent_role_relation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentRoleRelation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private Integer roleId;

	private Integer agentId;

}
