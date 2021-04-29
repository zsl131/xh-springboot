package com.zslin.core.qiniu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;


/**
 * 七牛配置
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "qiniu_qiniu_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QiniuConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 在七牛控制台在查看
	*/
	@NotBlank(message="accessKey不能为空")
	private String accessKey;

	/**
	* 在七牛控制台在查看
	*/
	@NotBlank(message="secretKey不能为空")
	private String secretKey;

	/**
	* domain文件域名
	*/
	@NotBlank(message="链接地址不能为空")
	private String url;

	@NotBlank(message="仓库名称不能为空")
	private String bucketName;

}
