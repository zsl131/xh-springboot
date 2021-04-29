package com.zslin.business.settlement.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 销售排名
 * @author 钟述林
 * @data generate on: 2020-09-04
 */
@Data
@Entity
@Table(name = "settlement_sale_ranking")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaleRanking implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private Integer agentId;

	private String agentName;

	private String agentPhone;

	private Integer customId;

	private String customNickname;

	private String customOpenid;

	/**
	* 归属年份
	*/
	private String belongYear;

	/**
	* 归属月份，yyyyMM
	*/
	private String belongMonth;

	/**
	* 名次
	*/
	private Integer orderNo;

	/**
	* 销售件数
	*/
	private Integer specsCount=0;

	/**
	* 销售金额
	*/
	private Float totalMoney=0f;

	/**
	* 提成总额
	*/
	private Float commissionMoney=0f;

}
