package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;


/**
 * 代理等级
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_agent_level")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentLevel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 等级名称
	* @remark 如：铜牌代理、银牌代理、金牌代理
	*/
	@NotBlank(message="等级名称不能为空")
	private String name;

	/**
	* 级别
	* @remark 数值越大，等级越高，提成标准越高
	*/
	private Integer level;

}
