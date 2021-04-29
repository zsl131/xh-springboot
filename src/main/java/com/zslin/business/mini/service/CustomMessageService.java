package com.zslin.business.mini.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.mini.tools.PushMessageTools;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.business.mini.dao.ICustomMessageDao;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.business.mini.model.CustomMessage;
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
 * Created by 钟述林 on 2020-05-06.
 */
@Service
@AdminAuth(name = "客服消息管理", psn = "移动端管理", orderNum = 2, type = "1", url = "/admin/customMessage")
@Explain(name = "客服消息管理", notes = "客服消息管理")
public class CustomMessageService {

    @Autowired
    private ICustomMessageDao customMessageDao;

    @Autowired
    private PushMessageTools pushMessageTools;

    @AdminAuth(name = "客服消息列表", orderNum = 1)
    @ExplainOperation(name = "客服消息列表", notes = "客服消息列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "客服消息数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "客服消息数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<CustomMessage> res = customMessageDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "获取客服消息", orderNum = 5)
    @ExplainOperation(name = "获取客服消息信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "客服消息ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            CustomMessage obj = customMessageDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    /** 回复 */
    public JsonResult reply(String params) {
        Integer id = JsonTools.getId(params);
        String reply = JsonTools.getJsonParam(params, "reply");
        CustomMessage cm = customMessageDao.findOne(id);

        cm.setReply(reply);
        cm.setReplyDay(NormalTools.curDate());
        cm.setReplyTime(NormalTools.curDatetime());
        cm.setReplyLong(System.currentTimeMillis());

        customMessageDao.save(cm);

        pushMessageTools.sendTextMsg(cm.getOpenid(), reply); //回复
        return JsonResult.success("回复成功");
    }
}
