package com.zslin.core.dto;

import lombok.Data;

/**
 * 佣金DTO对象
 */
@Data
public class RateDto {

    /** 产品 ID */
    private Integer proId;

    /** 产品标题 */
    private String proTitle;

    /** 规格ID */
    private Integer specsId;

    /** 规格名称 */
    private String specsName;

    /** 铜牌佣金 */
    private Float rate;

    /** 规格名称 */
    private String unitName;
}
