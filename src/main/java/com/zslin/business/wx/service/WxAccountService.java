package com.zslin.business.wx.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.business.wx.dao.IWxAccountDao;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.business.wx.model.WxAccount;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import com.zslin.core.validate.ValidationDto;
import com.zslin.core.validate.ValidationTools;
import com.zslin.core.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.zslin.core.tools.MyBeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2020-04-14.
 */
@Service
@AdminAuth(name = "微信用户管理", psn = "微信管理", orderNum = 2, type = "1", url = "/admin/wxAccount")
@Explain(name = "微信用户管理", notes = "微信用户管理")
@HasTemplateMessage
public class WxAccountService {

    @Autowired
    private IWxAccountDao wxAccountDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @AdminAuth(name = "微信用户列表", orderNum = 1)
    @ExplainOperation(name = "微信用户列表", notes = "微信用户列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "微信用户数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "微信用户数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<WxAccount> res = wxAccountDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "获取微信用户", orderNum = 5)
    @ExplainOperation(name = "获取微信用户信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "微信用户ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            WxAccount obj = wxAccountDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    /** 修改用户类型 */
    @TemplateMessageAnnotation(name = "身份级别变更通知", keys = "变动前级别-变动后级别-时间")
    public JsonResult updateType(String params) {
        try {
            Integer id = JsonTools.getId(params);
            String type = JsonTools.getJsonParam(params, "type");
            String oldType = JsonTools.getJsonParam(params, "oldType");
            String openid = JsonTools.getJsonParam(params, "openid");
            wxAccountDao.updateType(type, id);

            sendTemplateMessageTools.send2Wx(openid, "身份级别变更通知", "", "您的身份发生了变化",
                    TemplateMessageTools.field("变动前级别", WxAccountTools.genTypeName(oldType)),
                    TemplateMessageTools.field("变动后级别", WxAccountTools.genTypeName(type)),
                    TemplateMessageTools.field("时间", NormalTools.curDatetime()),
                    TemplateMessageTools.field("请知晓"));
            return JsonResult.success("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }


    }
}
