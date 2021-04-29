package com.zslin.business.mini.dto;

import lombok.Data;

/**
 * 代理提成DTO对象
 */
@Data
public class AgentCommissionDto {

    private Integer agentId;

    private double money=0d;

    private long totalCount=0L;

    private String status;

    private String haveType;

    private String name;

    public AgentCommissionDto(Integer agentId, String haveType, String status, Double money, Long totalCount) {
        this.agentId = agentId;
        this.haveType = haveType;
        this.status = status;
        this.money = money==null?0d:money;
        this.totalCount = totalCount==null?0L:totalCount;

    }
}
