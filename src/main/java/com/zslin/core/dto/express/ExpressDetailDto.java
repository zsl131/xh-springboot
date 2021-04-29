package com.zslin.core.dto.express;

import lombok.Data;

/**
 * 物流详情DTO
 * {"time":"2018-03-09 11:59:26","status":"【石家庄市】 快件已在 【长安三部】 签收,签收人: 本人, 感谢使用中通快递,期待再次为您服务!"}
 */
@Data
public class ExpressDetailDto {

    /** 记录时间 */
    private String time;

    /** 当前状态 */
    private String status;

    public ExpressDetailDto(String time, String status) {
        this.time = time;
        this.status = status;
    }

}
