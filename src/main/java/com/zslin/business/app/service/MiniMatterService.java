package com.zslin.business.app.service;

import com.zslin.business.dao.IMatterApplyDao;
import com.zslin.business.model.MatterApply;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 物料申请
 */
@Service
@HasTemplateMessage
public class MiniMatterService {

    @Autowired
    private IMatterApplyDao matterApplyDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    public JsonResult index(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        List<MatterApply> list = matterApplyDao.findByCustomId(customDto.getCustomId());
        return JsonResult.success().set("list", list);
    }

    /** 申请物料 */
    @TemplateMessageAnnotation(name = "申请审核通知", keys = "申请人-申请内容")
    public JsonResult save(String params) {
        try {
            WxCustomDto customDto = JsonTools.getCustom(params);
            Integer id = JsonTools.getId(params);
            String phone = JsonTools.getJsonParam(params, "phone");
            String shopName = JsonTools.getJsonParam(params, "shopName");
            String email = JsonTools.getJsonParam(params, "email");
            String remark = JsonTools.getJsonParam(params, "remark");
            MatterApply ma;
            if(id==null || id<=0) {
                ma = new MatterApply();
                ma.setCreateLong(System.currentTimeMillis());
                ma.setCreateTime(NormalTools.curDatetime());
                ma.setCreateDay(NormalTools.curDate());
                ma.setOpenid(customDto.getOpenid());
                ma.setNickname(customDto.getNickname());
                ma.setHeadImgUrl(customDto.getHeadImgUrl());
                ma.setCustomId(customDto.getCustomId());
            } else {
                ma = matterApplyDao.findOne(id);
            }
            ma.setStatus("0");
            ma.setShopName(shopName);
            ma.setRemark(remark);
            ma.setPhone(phone);
            ma.setEmail(email);
            matterApplyDao.save(ma);

            sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "申请审核通知", "", "新物料申请",
                    TemplateMessageTools.field("申请人",ma.getShopName()+"-"+ma.getPhone()),
                    TemplateMessageTools.field("申请内容", ma.getEmail()),
                    TemplateMessageTools.field("请及时登陆后台查看审核"));

            return JsonResult.success("保存成功");
        } catch (Exception e) {
            return JsonResult.error(e.getMessage());
        }
    }

    public JsonResult delete(String params) {
        try {
            WxCustomDto customDto = JsonTools.getCustom(params);
            Integer id = JsonTools.getId(params);
            MatterApply ma = matterApplyDao.findOne(id);
            JsonResult result = JsonResult.getInstance();
            if(!"0".equals(ma.getStatus())) {
                result.set("flag", "0").set("message", "当前状态不可删除");
            } else {
                if(!customDto.getCustomId().equals(ma.getCustomId())) {
                    result.set("flag", "0").set("message", "没有权限删除");
                } else {
                    matterApplyDao.delete(ma);
                    result.set("flag", "1").set("message", "删除成功");
                }
            }
            return result;
        } catch (Exception e) {
            return JsonResult.success("删除成功");
        }
    }
}
