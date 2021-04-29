package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import org.hibernate.validator.constraints.Range;


/**
 * 代理提成标准
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_agent_level_specs_rate")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentLevelSpecsRate implements Serializable {

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
	* 产品ID
	*/
	private Integer proId;

	/**
	* 产品标题
	*/
	private String proTitle;

	/**
	* 产品规格ID
	*/
	private Integer specsId;

	/**
	* 产品规格名称
	*/
	private String specsName;

	/**
	* 提成金额
	*/
	@Range(min=0, message="提成金额不能小于0")
	private Float amount;

	/**
	* 上级提成金额
	*/
	private Float leaderAmount;

}
