package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 物流接口配置
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_express_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpressConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 请求地址
	*/
	private String url;

	/**
	* 接口路径
	*/
	private String path;

	/**
	* 接口方提供代码
	*/
	private String appCode;

	/**
	* 接口方名称
	*/
	private String name;

}
