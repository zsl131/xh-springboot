package com.zslin.business.app.service;

import com.zslin.business.dao.IMediumDao;
import com.zslin.business.dao.IPhonographDao;
import com.zslin.business.model.Medium;
import com.zslin.business.model.Phonograph;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.tools.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 留声机
 */
@Service
@Slf4j
public class MiniPhonographService {

    @Autowired
    private IPhonographDao phonographDao;

    @Autowired
    private IMediumDao mediumDao;

    /**
     * 添加留声机
     * @param params
     * @return
     */
    @NeedAuth(openid = true)
    public JsonResult add(String params) {
        try {
            String openid = JsonTools.getOpenid(params);
            String nickname = JsonTools.getNickname(params);
            String ordersNo = JsonTools.getJsonParam(params, "ordersNo"); //订单编号
            String password = JsonTools.getJsonParam(params, "password"); //密码
            String ticket = JsonTools.getJsonParam(params, "ticket"); //录音票据
            Medium medium = mediumDao.findByTicket(ticket);
            String url = medium.getRootUrl() + "/" + medium.getQiniuKey();
            Phonograph p = new Phonograph();
            p.setCreateDay(NormalTools.curDate());
            p.setCreateTime(NormalTools.curDatetime());
            p.setCreateLong(System.currentTimeMillis());
            p.setNickname(nickname);
            p.setOpenid(openid);
            p.setOrdersNo(ordersNo);
            p.setPassword(password);
            p.setUrl(url);
            phonographDao.save(p);
            return JsonResult.success("保存成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return JsonResult.error(e.getMessage());
        }
    }
}
