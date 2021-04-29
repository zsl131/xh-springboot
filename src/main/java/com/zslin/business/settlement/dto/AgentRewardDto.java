package com.zslin.business.settlement.dto;

import com.zslin.core.common.NormalTools;
import lombok.Data;

/**
 * 代理奖金DTO对象
 */
@Data
public class AgentRewardDto {

    /** 奖金总额 */
    private double totalExtraMoney = 0d;

    /** 已领取的奖金总额 */
    private double totalReceiptMoney = 0d;

    /** 剩余可领取的奖金总额 */
    private double totalSurplusMoney = 0d;

    public AgentRewardDto() {
    }

    public AgentRewardDto(Double totalExtraMoney, Double totalReceiptMoney, Double totalSurplusMoney) {
        this.totalExtraMoney = NormalTools.retain2Decimal(totalExtraMoney);
        this.totalReceiptMoney = NormalTools.retain2Decimal(totalReceiptMoney);
        this.totalSurplusMoney = NormalTools.retain2Decimal(totalSurplusMoney);
    }
}
