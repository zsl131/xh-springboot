package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 退款原因
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_refund_reason")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefundReason implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 原因
	*/
	private String reason;

	/**
	* 序号
	*/
	private Integer orderNo;

}
