package com.zslin.business.agent.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 代理规格
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "agent_agent_specs")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentSpecs implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private Integer roleId;

	private String roleName;

	/**
	* 对应规格
	*/
	private Integer specsId;

	private String specsName;

	private String specsRemark;

	/**
	* 零售价
	*/
	private Float oriPrice;

	/**
	* 代理价
	*/
	private Float price;

	/**
	* 产品ID
	*/
	private Integer proId;

	/**
	* 产品标题
	*/
	private String priTitle;

}
