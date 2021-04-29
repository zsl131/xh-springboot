package com.zslin.business.mini.dto;

import com.zslin.business.mini.model.UnifiedOrder;
import lombok.Data;

/** 提交支付的DTO对象 */
@Data
public class PaySubmitDto {

    private String appId;

    private String timeStamp;

    private String nonceStr;

    private String packageStr;

    private String paySign;

    private String signType = "MD5";

    private String flag; //1-表示可支付；0-表示不可支付

    /** 统一订单 */
    private UnifiedOrder unifiedOrder;
}
