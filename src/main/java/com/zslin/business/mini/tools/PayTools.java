package com.zslin.business.mini.tools;

import com.github.wxpay.sdk.MyPayConfig;
import com.github.wxpay.sdk.WXPay;
import com.zslin.business.dao.ICustomCommissionRecordDao;
import com.zslin.business.dao.IOrdersDao;
import com.zslin.business.dao.IOrdersProductDao;
import com.zslin.business.dao.IRefundRecordDao;
import com.zslin.business.mini.dao.IUnifiedOrderDao;
import com.zslin.business.mini.dto.PaySubmitDto;
import com.zslin.business.mini.dto.RefundDto;
import com.zslin.business.mini.model.MiniConfig;
import com.zslin.business.mini.model.UnifiedOrder;
import com.zslin.business.model.CustomCommissionRecord;
import com.zslin.business.model.Orders;
import com.zslin.business.model.OrdersProduct;
import com.zslin.business.model.RefundRecord;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.LoginUserDto;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.tools.ConfigTools;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.RandomTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信支付工具类
 */
@Component("payTools")
@Slf4j
@HasTemplateMessage
public class PayTools {

    @Autowired
    private MiniConfigTools miniConfigTools;

    @Autowired
    private IOrdersDao ordersDao;

    private final String BODY_PRE = "满山晴";

    @Autowired
    private ConfigTools configTools;

    @Autowired
    private IUnifiedOrderDao unifiedOrderDao;

    @Autowired
    private IRefundRecordDao refundRecordDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @Autowired
    private IOrdersProductDao ordersProductDao;

    @Autowired
    private ICustomCommissionRecordDao customCommissionRecordDao;

    public RefundDto refund(Integer ordersProId, Float money, String reason, LoginUserDto user, boolean needAddRecord) {
        RefundDto refundDto = new RefundDto();

        OrdersProduct ordersProduct = ordersProductDao.findOne(ordersProId);
        Orders orders = ordersDao.findByOrdersNo(ordersProduct.getOrdersNo());

        //获取微信小程序配置文件
        MiniConfig config = miniConfigTools.getMiniConfig();
        Map<String, String> data = new HashMap<>();

        String appId = config.getAppid();
        String apiKey = config.getApiKey();

        String body;
        String proTitles = orders.getProTitles();
        if(proTitles==null || "".equals(proTitles.trim())) {
            body = BODY_PRE+"-"+orders.getSpecsCount()+" 件产品"; //支付名称
        } else {body = proTitles;}

        //退款单号
        String refundNo = orders.getId()+"-"+ordersProduct.getId()+"-"+RandomTools.genCodeNew();
        RefundRecord rr = refundRecordDao.queryRefundRecord(orders.getOrdersNo());
        if(rr!=null && "-1".equals(rr.getStatus())) { //如果上一次是失败的
            refundNo = rr.getRefundNo();
        }

        String nonceStr = RandomTools.randomString(32);

        String sign = PayUtils.buildSign(appId, config.getMchid(), body, apiKey, nonceStr);
        data.put("appid", appId);
        data.put("mch_id", config.getMchid());
        data.put("nonce_str", nonceStr);
        data.put("sign", sign);
        data.put("out_trade_no", orders.getOrdersNo());
        data.put("out_refund_no", refundNo); //TODO 退款单号
        data.put("total_fee", buildTotalMoney((orders.getTotalMoney()-(orders.getAutoCommissionMoney()==null?0:orders.getAutoCommissionMoney())-(orders.getDiscountMoney()==null?0:orders.getDiscountMoney())))); //总金额，分
        data.put("refund_fee", buildTotalMoney(money));

        try {
            String certPath = configTools.getFilePath("cert") + "apiclient_cert.p12";

            MyPayConfig payConfig = new MyPayConfig(certPath, config);
            WXPay wxpay = new WXPay(payConfig);
            Map<String, String> rMap = wxpay.refund(data);

//System.out.println("----PayTools----->" + rMap);

            String return_code = rMap.get("return_code");
            String result_code = rMap.get("result_code");
            String err_code = rMap.get("err_code");
            String err_code_des = rMap.get("err_code_des");

            refundDto.setErrCode(err_code);
            refundDto.setErrCodeDes(err_code_des);
            String status = "-1";

            boolean suc = false;
            //表示成功
            if ("SUCCESS".equals(return_code) && return_code.equals(result_code)) {
                handleRefund(ordersProduct, orders, money);
                suc = true;
                status = "0";
            } else if("订单已全额退款".equals(err_code_des)) {
                List<OrdersProduct> ordersProductList = ordersProductDao.findByOrdersNo(orders.getOrdersNo());
                //如果是全额退款，则全部退掉
                for(OrdersProduct op : ordersProductList) {
                    Float backMoney = op.getPrice()*op.getAmount() - op.getBackMoney();
                    if(backMoney>(orders.getTotalMoney()-(orders.getAutoCommissionMoney()==null?0:orders.getAutoCommissionMoney())-(orders.getDiscountMoney()==null?0:orders.getDiscountMoney())-orders.getBackMoney())) {
                        backMoney = orders.getTotalMoney()-(orders.getAutoCommissionMoney()==null?0:orders.getAutoCommissionMoney())-(orders.getDiscountMoney()==null?0:orders.getDiscountMoney())-orders.getBackMoney();}
                    handleRefund(op, orders, backMoney);
                }
            }
            if(needAddRecord) {
                addRecord(orders, ordersProduct, user, money, refundNo, reason, status, err_code, err_code_des); //保存退款记录
            }

            refundDto.setStatus(status);

            //订单编号-产品名称-退款金额-退款原因-退款时间
            sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "退款成功通知", "", suc?"退款成功啦":"退款失败",
                    TemplateMessageTools.field("订单编号", orders.getOrdersNo()),
                    TemplateMessageTools.field("产品名称", ordersProduct.getProTitle()),
                    TemplateMessageTools.field("退款金额", suc?(money+""):"退款失败"),
                    TemplateMessageTools.field("退款原因", suc?reason:(err_code+":"+err_code_des)),
                    TemplateMessageTools.field("退款时间", NormalTools.curDatetime()),

                    TemplateMessageTools.field("操作人员【"+user.getNickname()+"】\\n"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return refundDto;
    }

    /**
     * 退款申请
     * Orders orders, OrdersProduct ordersProduct, LoginUserDto user, Float backMoney, String reason
     */
    @TemplateMessageAnnotation(name = "退款成功通知", keys = "订单编号-产品名称-退款金额-退款原因-退款时间")
    public RefundDto refund(String params, LoginUserDto user) {
        Integer ordersProId = JsonTools.getParamInteger(params, "ordersProId");
        Float money = Float.parseFloat(JsonTools.getJsonParam(params, "money"));
        String reason = JsonTools.getJsonParam(params, "reason"); //退款原因
        return refund(ordersProId, money, reason, user, true);
    }

    private void handleRefund(OrdersProduct ordersProduct, Orders orders, Float money) {

        //处理订单产品
//        OrdersProduct ordersProduct = ordersProductDao.findOne(ordersProId);
//        Orders orders = ordersDao.findByOrdersNo(ordersProduct.getOrdersNo());
        ordersProduct.setHasAfterSale("1");
        ordersProduct.setBackMoney((ordersProduct.getBackMoney()==null?0:ordersProduct.getBackMoney())+money);
        ordersProduct.setStatus("-2"); //有售后
        ordersProduct.setBackDay(NormalTools.curDate());
        ordersProduct.setBackTime(NormalTools.curDatetime());
        ordersProduct.setBackLong(System.currentTimeMillis());
        ordersProductDao.save(ordersProduct);

        //处理订单信息
        orders.setHasAfterSale("1");
//        orders.setStatus("-2"); //有售后
        orders.setSaleFlag("1"); //有售后
        orders.setBackMoney((orders.getBackMoney()==null?0:orders.getBackMoney()) + money);
        Float realMoney = orders.getTotalMoney() - (orders.getDiscountMoney()==null?0:orders.getDiscountMoney()) -
                (orders.getAutoCommissionMoney()==null?0:orders.getAutoCommissionMoney())- orders.getBackMoney();
        if(realMoney<=0) {
            orders.setStatus("-1");
            ordersProductDao.updateStatus("-1", orders.getOrdersNo());
        } //如果退完了就直接关闭
        ordersDao.save(orders);

        Float tmpMoney = ordersProduct.getPrice()*ordersProduct.getAmount(); //实际支付金额
        tmpMoney = tmpMoney * 0.12f; //只要退款金额超过实付金额的12%，就不能有提成了
        //TODO 还需要处理提成信息
        //如果退款金额超过12%，则取消代理提成
        List<CustomCommissionRecord> recordList = customCommissionRecordDao.findByOrdersNoAndProId(orders.getOrdersNo(), ordersProduct.getProId());
        for(CustomCommissionRecord ccr : recordList) {
            if(ordersProduct.getBackMoney()>=tmpMoney) {
//                ccr.setStatus("-2"); //设置为售后件
                ccr.setSaleFlag("2"); //售后；不可提现
            } else {
                ccr.setSaleFlag("1"); //售后；可提现
            }
            customCommissionRecordDao.save(ccr);
        }
    }

    //保存退款记录
    private void addRecord(Orders orders, OrdersProduct product, LoginUserDto user, Float backMoney, String refundNo,
                           String reason, String status, String resCode, String resCodeDes) {
        RefundRecord rr = new RefundRecord();
        rr.setAgentName(orders.getAgentName());
        rr.setAgentOpenid(orders.getAgentOpenid());
        rr.setAgentPhone(orders.getAgentPhone());
        rr.setBackMoney(backMoney);
        rr.setCreateDay(NormalTools.curDate());
        rr.setCreateLong(System.currentTimeMillis());
        rr.setCreateTime(NormalTools.curDatetime());
        rr.setOrdersId(orders.getId());
        rr.setOrdersNo(orders.getOrdersNo());
        rr.setOrdersProId(product.getProId());
        rr.setOrdersProTitle(product.getProTitle());

        rr.setOptName(user.getNickname());
        rr.setOptUserId(user.getId());
        rr.setOptUsername(user.getUsername());
        rr.setRefundNo(refundNo);
        rr.setReason(reason);
        rr.setStatus(status);
        rr.setResCode(resCode);
        rr.setResCodeDes(resCodeDes);
        rr.setType("0");

        refundRecordDao.save(rr);
    }

    /**
     * 统一下单接口
     * @return 返回一个可以直接到小程序进行支付的DTO对象
     */
    public PaySubmitDto unifiedOrder(WxCustomDto customDto, String ip, String ordersNo) {
        UnifiedOrder resOrder = new UnifiedOrder();
        //Map resultMap=new HashMap();
        Orders orders = ordersDao.findByOrdersNo(ordersNo);
        //获取微信小程序配置文件
        MiniConfig config = miniConfigTools.getMiniConfig();
        Map<String, String> data = new HashMap<>();

        String appId = config.getAppid();
        String apiKey = config.getApiKey();

        String nonceStr = RandomTools.randomString(32);
        String body;
        String proTitles = orders.getProTitles();
        if(proTitles==null || "".equals(proTitles.trim())) {
            body = BODY_PRE+"-"+orders.getSpecsCount()+" 件产品"; //支付名称
        } else {body = proTitles;}
        Float money = orders.getTotalMoney();
        if(orders.getDiscountMoney()!=null && orders.getDiscountMoney()>0) {
            money = orders.getTotalMoney() - orders.getDiscountMoney();
        }
        if(orders.getAutoCommissionMoney()!=null && orders.getAutoCommissionMoney()>0) {
            money -= orders.getAutoCommissionMoney();
        }
        String sign = PayUtils.buildSign(appId, config.getMchid(), body, apiKey, nonceStr);
        data.put("appid", appId);
        data.put("mch_id", config.getMchid());
        data.put("nonce_str", nonceStr);
        data.put("body", body);
        data.put("out_trade_no",ordersNo);
        data.put("total_fee", buildTotalMoney(money));
        data.put("spbill_create_ip", ip);
        data.put("notify_url", config.getPayNotifyUrl()); //支付结果通知地址
        data.put("trade_type","JSAPI"); //交易类型，小程序填：JSAPI
        data.put("openid", customDto.getOpenid());
        data.put("sign", sign);

        resOrder.setPayMoney(orders.getTotalMoney()); //支付金额
        resOrder.setCustomId(customDto.getCustomId());
        resOrder.setHeadImgUrl(customDto.getHeadImgUrl());
        resOrder.setNickname(customDto.getNickname());
        resOrder.setOpenid(customDto.getOpenid());
        resOrder.setOrdersId(orders.getId());
        resOrder.setOrdersNo(orders.getOrdersNo());

        try {

            String certPath = configTools.getFilePath("cert") + "apiclient_cert.p12";

            MyPayConfig payConfig = new MyPayConfig(certPath, config);
            WXPay wxpay = new WXPay(payConfig);

            Map<String, String> rMap = wxpay.unifiedOrder(data);
            //System.out.println("统一下单接口返回: " + rMap);
            //log.info(rMap.toString()); //显示结果
            //  err_code=ORDERPAID, return_msg=OK, result_code=FAIL, err_code_des=??????
            String return_code = rMap.get("return_code");
            String result_code = rMap.get("result_code");
            String err_code = rMap.get("err_code");
            String err_code_des = rMap.get("err_code_des");
            resOrder.setErrCode(err_code);
            resOrder.setErrCodeDes(err_code_des);
            /*resultMap.put("nonceStr", nonceStr);*/
            //Long timeStamp = System.currentTimeMillis() / 1000;
            if ("SUCCESS".equals(return_code) && return_code.equals(result_code)) {
                String prepayid = rMap.get("prepay_id"); //预支付订单ID

                resOrder.setPrepayId(prepayid);
                resOrder.setStatus("0"); //表示获取成功
            } else if("ORDERPAID".equalsIgnoreCase(err_code)) { //如果是支付，则修改订单状态
                resOrder.setStatus("0");
                hasPayed(orders); //
            } else {
//                return  response;
                resOrder.setStatus("-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
//            return  response;
            resOrder.setStatus("-2");
        }

        //String status = resOrder.getStatus();
//        log.info(resOrder.toString());
        resOrder.setCreateDay(NormalTools.curDate());
        resOrder.setCreateTime(NormalTools.curDatetime());
        resOrder.setCreateLong(System.currentTimeMillis());
        unifiedOrderDao.save(resOrder); //存入数据库
        //在没有出错且prepayId存在时返回DTO
        return buildSubmitData(appId, apiKey, resOrder);
    }

    /**
     * 支付成功调用此方法
     */
    public void hasPayed(String ordersNo) {
        Orders orders = ordersDao.findByOrdersNo(ordersNo);
        hasPayed(orders);
    }

    /** 支付成功调用此方法 */
    @TemplateMessageAnnotation(name = "订单付款成功通知", keys = "订单号-支付时间-支付金额-支付方式")
    public void hasPayed(Orders orders) {
        String ordersNo = orders.getOrdersNo();
        orders.setStatus("1");
        String payDay = NormalTools.curDate();
        String payTime = NormalTools.curDatetime();
        Long payLong = System.currentTimeMillis();
        orders.setPayTime(payTime);
        orders.setPayDay(payDay);
        orders.setPayLong(payLong);

        Float discountMoney = orders.getDiscountMoney();
        discountMoney = (discountMoney == null) ? 0 : discountMoney;

        Float autoCommissionMoney = orders.getAutoCommissionMoney()==null?0:orders.getAutoCommissionMoney();

        orders.setPayMoney(orders.getTotalMoney() - discountMoney - autoCommissionMoney); //totalMoney就是支付金额
        ordersDao.save(orders);
//                ordersDao.updateStatus("1", ordersNo, customDto.getCustomId()); //修改订单状态
        customCommissionRecordDao.updateStatusByNormal("1", ordersNo); //修改提成状态，普通提成记录
        customCommissionRecordDao.updateStatusByAuto("4", "AUTO", ordersNo); //修改提成状态，自动佣金抵扣
        ordersProductDao.updatePayDay(payDay, payTime, payLong, ordersNo); //修改订单产品状态

        sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "订单付款成功通知", "", orders.getProTitles(),
                TemplateMessageTools.field("订单号", orders.getOrdersNo()),
                TemplateMessageTools.field("支付时间", orders.getPayTime()),
                TemplateMessageTools.field("支付金额", (orders.getPayMoney())+ " 元"),
                TemplateMessageTools.field("支付方式", "在线支付"),

                TemplateMessageTools.field("请核对信息后尽快处理["+ MiniUtils.buildAgent(orders)+"]"));
    }

    /**
     * 生成调起支付的DTO对象
     * @return
     */
    private PaySubmitDto buildSubmitData(String appId, String apiKey, UnifiedOrder unifiedOrder) {
        String prepayId = unifiedOrder.getPrepayId();
        String status = unifiedOrder.getStatus();
        String nonceStr = RandomTools.randomString(32);
        String timestamp = (System.currentTimeMillis() / 1000)+"";
        String sign = PayUtils.buildPaySign(appId, nonceStr, prepayId, timestamp, apiKey);

        PaySubmitDto dto = new PaySubmitDto();
        dto.setAppId(appId);
        dto.setNonceStr(nonceStr);
        dto.setPackageStr("prepay_id="+prepayId);
        dto.setTimeStamp(timestamp);
        dto.setSignType("MD5");
        dto.setPaySign(sign);

        if("0".equals(status) && prepayId!=null && !"".equals(prepayId)) {
            dto.setFlag("1");
        } else {dto.setFlag("0");}
        dto.setUnifiedOrder(unifiedOrder);

        //log.info(dto.toString());
        return dto;
    }

    /** 把订单金额换成分 */
    private String buildTotalMoney(Float totalMoney) {
        return String.valueOf((int)(totalMoney*100));
    }
}
