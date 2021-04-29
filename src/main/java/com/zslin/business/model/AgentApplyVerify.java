package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 代理申请审核
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_agent_apply_verify")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentApplyVerify implements Serializable {

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
	* 审核人员信息
	*/
	private String verifyOperator;

	/**
	* 审核日期
	* @remark 格式：yyyy-MM-dd
	*/
	private String verifyDay;

	/**
	* 审核时间
	* @remark 格式：yyyy-MM-dd HH:mm:ss
	*/
	private String verifyTime;

	/**
	* 审核时间Long类型
	*/
	private Long verifyLong;

	/**
	* 审核内容
	*/
	@Lob
	private String content;

	/**
	* 审核结果
	* @remark 1-通过审核；2-驳回申请
	*/
	private String verifyRes;

}
