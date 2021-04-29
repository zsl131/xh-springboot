package com.zslin.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 基金
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "business_fund")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fund implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	/**
	* 总基金笔数
	*/
	private Integer totalCount;

	/**
	* 总基金金额
	*/
	private Float totalMoney;

}
