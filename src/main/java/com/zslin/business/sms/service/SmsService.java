package com.zslin.business.sms.service;

import com.zslin.business.dao.ICustomerDao;
import com.zslin.business.model.Customer;
import com.zslin.business.sms.tools.SmsTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.RandomTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zsl on 2018/9/26.
 */
@Service
public class SmsService {

    @Autowired
    private SmsTools smsTools;

    @Autowired
    private ICustomerDao customerDao;

    /**
     * 后台绑定手机号码
     * @param params {phone:''}
     * @return
     */
    public JsonResult sendCode(String params) {
        try {
            String phone = JsonTools.getJsonParam(params, "phone");
            Customer c = customerDao.findByPhone(phone);
            if(c!=null && c.getId()>0) { //如果该手机号码已经绑定用户名
                return JsonResult.success("该手机已绑定用户："+c.getNickname()).set("flag", "0");
            }
            String code = RandomTools.genCode4();
            smsTools.sendMsg(phone, "code", code);
            return JsonResult.success("发送成功").set("code", code).set("phone", phone).set("flag", "1");
        } catch (Exception e) {
            return JsonResult.error(e.getMessage());
        }
    }
}
