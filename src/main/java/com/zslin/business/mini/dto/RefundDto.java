package com.zslin.business.mini.dto;

import lombok.Data;

/**
 * 退款DTO对象
 */
@Data
public class RefundDto {

    private String errCode;

    private String errCodeDes;

    /** 0-成功；-1-失败 */
    private String status;

}
