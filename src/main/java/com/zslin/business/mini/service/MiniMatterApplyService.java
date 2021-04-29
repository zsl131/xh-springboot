package com.zslin.business.mini.service;

import com.zslin.business.dao.IMatterApplyDao;
import com.zslin.business.model.MatterApply;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 物料申请
 *  - 用于实体店铺申请推广图片
 */
@Service
@HasTemplateMessage
public class MiniMatterApplyService {

    @Autowired
    private IMatterApplyDao matterApplyDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @NeedAuth(openid = true)
    @TemplateMessageAnnotation(name = "申请审核通知", keys = "申请人-申请内容")
    public JsonResult apply(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);

        String shopName = JsonTools.getJsonParam(params, "shopName");
        String phone = JsonTools.getJsonParam(params, "phone");
        String remark = JsonTools.getJsonParam(params, "remark");
        String email = JsonTools.getJsonParam(params, "email");

        MatterApply apply  = new MatterApply();
        apply.setCustomId(customDto.getCustomId());
        apply.setEmail(email);
        apply.setHeadImgUrl(customDto.getHeadImgUrl());
        apply.setNickname(customDto.getNickname());
        apply.setOpenid(customDto.getOpenid());
        apply.setPhone(phone);
        apply.setRemark(remark);
        apply.setShopName(shopName);
        apply.setStatus("0");
        apply.setCreateDay(NormalTools.curDate());
        apply.setCreateTime(NormalTools.curDatetime());
        apply.setCreateLong(System.currentTimeMillis());

        matterApplyDao.save(apply);

        sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "申请审核通知", "", customDto.getNickname()+" 提交了物料申请",
                TemplateMessageTools.field("申请人", customDto.getNickname()),
                TemplateMessageTools.field("申请内容", apply.getPhone()+"-"+apply.getShopName()),
                TemplateMessageTools.field("请及时登陆后台查看制作物料"));
        return JsonResult.success("申请成功");
    }

    public JsonResult list(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        List<MatterApply> applyList = matterApplyDao.findByCustomId(customDto.getCustomId());
        return JsonResult.success().set("applyList", applyList);
    }
}
