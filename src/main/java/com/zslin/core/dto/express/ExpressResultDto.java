package com.zslin.core.dto.express;

import lombok.Data;

import java.util.List;

/**
 * 物流结果信息
 * {"status":"0","msg":"ok","result":{"number":"780098068058","type":"zto","list":[],"deliverystatus":"3","issign":"1",
 * "expName":"中通快递","expSite":"www.zto.com","expPhone":"95311","courier":"容晓光","courierPhone":"13081105270","updateTime":"2019-08-27 13:56:19","takeTime":"2天20小时14分","logo":"http://img3.fegine.com/express/zto.jpg"}}
 */
@Data
public class ExpressResultDto {

    /** status 0:正常查询 201:快递单号错误 203:快递公司不存在 204:快递公司识别失败 205:没有信息 207:该单号被限制，错误单号 */
    private String status;

    private String msg;

    /** 快递单号 */
    private String number;

    /** 快递公司代码 */
    private String type;

    /** 0：快递收件(揽件)1.在途中 2.正在派件 3.已签收 4.派送失败 5.疑难件 6.退件签收  */
    private String deliverystatus;

    /** 是否签收，1-是 */
    private String issign;

    /** 快递公司名称 */
    private String expName;

    /** 快递公司网址 */
    private String expSite;

    /** 快递公司电话 */
    private String expPhone;

    /** 快递员 */
    private String courier;

    /** 快递员电话 */
    private String courierPhone;

    /** 信息更新最新时间 */
    private String updateTime;

    /** 发货到收货所用时长 */
    private String takeTime;

    /** 物流轨迹信息 */
    private List<ExpressDetailDto> detailList;
}
