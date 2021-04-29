package com.zslin.business.mini.controller;

import com.github.wxpay.sdk.WXPayUtil;
import com.zslin.business.dao.ICustomCommissionRecordDao;
import com.zslin.business.dao.IOrdersDao;
import com.zslin.business.dao.IOrdersProductDao;
import com.zslin.business.mini.model.MiniConfig;
import com.zslin.business.mini.tools.MiniConfigTools;
import com.zslin.business.mini.tools.MiniUtils;
import com.zslin.business.mini.tools.PayNotifyTools;
import com.zslin.business.mini.tools.PayTools;
import com.zslin.business.model.Orders;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.common.NormalTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付控制器
 */
@Controller
@RequestMapping(value = "wxPay")
@Slf4j
@HasTemplateMessage
public class WXPayController {

    @Autowired
    private MiniConfigTools miniConfigTools;

    @Autowired
    private IOrdersDao ordersDao;

    @Autowired
    private IOrdersProductDao ordersProductDao;

    @Autowired
    private ICustomCommissionRecordDao customCommissionRecordDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @Autowired
    private PayTools payTools;

    /**
     * 支付结果通知地址
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "notify")
    //@TemplateMessageAnnotation(name = "订单付款成功通知", keys = "订单号-支付时间-支付金额-支付方式")
    public String notify(HttpServletRequest request, HttpServletResponse response) {

        MiniConfig miniConfig = miniConfigTools.getMiniConfig();

        Map<String, String> paramsToMap = PayNotifyTools.payRequest2Map(request);
        //log.info("微信回调参数map===>"+paramsToMap);
        //校验微信的sign值
        try {
            boolean flag = WXPayUtil.isSignatureValid(paramsToMap, miniConfig.getApiKey());
            if(flag){
                String ordersNo = paramsToMap.get("out_trade_no");
                String payResult = paramsToMap.get("result_code");
                if("SUCCESS".equalsIgnoreCase(payResult)) { //如果业务结果为成功
                    Orders orders = ordersDao.findByOrdersNo(ordersNo);
                    if ("0".equals(orders.getStatus())) { //如果是未支付状态
                        payTools.hasPayed(orders);
                    }
                }

                //如果订单修改成功,通知微信接口不要在回调这个接口
                responseXmlSuccess(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

   private void responseXmlSuccess(HttpServletResponse response) throws Exception {
        Map<String,String> map =new HashMap<>();
        map.put("return_code","SUCCESS");
        map.put("return_msg","OK");
        String xml = createSuccessXml();
        //log.info("微信异步回调结束====> "+xml);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter writer = response.getWriter();
        writer.write(xml);
        writer.flush();
    }

    private String createSuccessXml(String code, String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>").append("<return_code><![CDATA[").append(code).append("]]></return_code>")
                .append("<return_msg><![CDATA[").append(msg).append("]]></return_msg>").append("</xml>");
        return sb.toString();
    }

    private String createSuccessXml() {
        return createSuccessXml("SUCCESS", "OK");
    }
}
