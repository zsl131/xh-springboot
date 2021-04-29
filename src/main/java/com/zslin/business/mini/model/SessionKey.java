package com.zslin.business.mini.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 会话机制
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "mini_session_key")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionKey implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private Integer customId;

	/**
	* sessionKey
	*/
	private String sk;

	/**
	* 微信登陆code
	*/
	private String code;

	private String openid;

}
