package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 代理默认提成标准
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_agent_rate_default")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentRateDefault implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 等级ID
	*/
	private Integer levelId;

	/**
	* 等级名称
	*/
	private String levelName;

	/**
	* 默认提成标准
	*/
	private Float amount;

	/**
	* 上级默认提成标准
	*/
	private Float leaderAmount=0f;

}
