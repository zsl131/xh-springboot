package com.zslin.business.app.dto;

import lombok.Data;

/**
 * 订单佣金DTO对象
 */
@Data
public class OrdersCommissionDto {

    /** 规格ID */
    private Integer specsId;

    private String specsName;

    private Integer proId;

    private String proTitle;

    /** 佣金标准 */
    private Float rate;

    /** 上级佣金 */
    private Float leaderRate;

    public OrdersCommissionDto(Integer specsId, String specsName, Integer proId, String proTitle, Float rate, Float leaderRate) {
        this.specsId = specsId;
        this.specsName = specsName;
        this.proId = proId;
        this.proTitle = proTitle;
        this.rate = rate;
        this.leaderRate = leaderRate;
    }
/*public OrdersCommissionDto(Integer specsId, Float rate) {
        this.specsId = specsId; this.rate = rate;
    }*/
}
