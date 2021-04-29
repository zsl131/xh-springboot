package com.zslin.business.mini.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 客户消息订阅
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "mini_custom_subscribe")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomSubscribe implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private Integer customId;

	private String customNickname;

	private String customOpenid;

	private Integer messageId;

	private String messageName;

	private String messageSn;

	/**
	* 订阅状态
	* @remark 0-未订阅；1-订阅
	*/
	private String status;

}
