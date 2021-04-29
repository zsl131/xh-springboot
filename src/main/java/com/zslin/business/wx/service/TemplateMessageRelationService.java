package com.zslin.business.wx.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.wx.dao.ITemplateMessageRelationDao;
import com.zslin.business.wx.dto.TemplateMessageDto;
import com.zslin.business.wx.model.TemplateMessageRelation;
import com.zslin.business.wx.tools.TemplateMessageAnnotationTools;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.PinyinToolkit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 钟述林 on 2020-04-14.
 */
@Service
@AdminAuth(name = "微信模板消息管理", psn = "微信管理", orderNum = 2, type = "1", url = "/admin/templateMessageRelation")
@Explain(name = "微信模板消息管理", notes = "微信模板消息管理")
public class TemplateMessageRelationService {

    @Autowired
    private ITemplateMessageRelationDao templateMessageRelationDao;

    @Autowired
    private TemplateMessageAnnotationTools templateMessageAnnotationTools;

    public JsonResult noConfig(String params) {
        List<TemplateMessageDto> list = templateMessageAnnotationTools.findNoConfigTemplateMessage();
        return JsonResult.getInstance().set("list", list);
    }

    /** 全部已配置的数据 */
    public JsonResult list(String params) {
        List<TemplateMessageRelation> configed = templateMessageRelationDao.findAll();
        List<TemplateMessageDto> noConfig = templateMessageAnnotationTools.findNoConfigTemplateMessage();
        return JsonResult.getInstance().set("noConfig", noConfig).set("configed", configed);
    }

    public JsonResult add(String params) {
        try {
            TemplateMessageRelation tmr = JSONObject.toJavaObject(JSON.parseObject(params), TemplateMessageRelation.class);
            tmr.setTemplatePinyin(PinyinToolkit.cn2Spell(tmr.getTemplateName(), ""));
            templateMessageRelationDao.save(tmr);
            return JsonResult.success("配置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            templateMessageRelationDao.deleteById(id);
            return JsonResult.success("删除成功");
        } catch (NumberFormatException e) {
            return JsonResult.error(e.getMessage());
        }
    }

}
