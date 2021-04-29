package com.zslin.business.settlement.dto;

import lombok.Data;

@Data
public class RankingDto {

    private Integer agentId;

    private String agentName;

    private String agentPhone;

    private Integer customId;

    private String customNickname;

    private String openid;

    private long specsCount=0;

    private double commissionMoney=0d;

    public RankingDto(Integer agentId, String agentName, String agentPhone, Integer customId, String customNickname,
            String openid, Long specsCount, Double commissionMoney) {
        this.agentId = agentId;
        this.agentName = agentName;
        this.agentPhone = agentPhone;
        this.customId = customId;
        this.customNickname = customNickname;
        this.openid = openid;
        this.specsCount = specsCount==null?0:specsCount;
        this.commissionMoney = commissionMoney==null?0:commissionMoney;
    }
}
