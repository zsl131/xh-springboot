package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 物料申请
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_matter_apply")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatterApply implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String nickname;

	private String openid;

	private String headImgUrl;

	private Integer customId;

	private String phone;

	/**
	* 店铺名称
	*/
	private String shopName;

	/**
	* 邮箱
	*/
	private String email;

	/**
	* 说明
	*/
	@Lob
	private String remark;

	/**
	* 状态
	* @remark 0-申请；1-已发邮箱
	*/
	private String status;

	private String createDay;

	private String createTime;

	private Long createLong;

}
