package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 催单
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_remind_orders")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemindOrders implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String ordersNo;

	private Integer ordersId;

	private String createDay;

	private String createTime;

	private Long createLong;

}
