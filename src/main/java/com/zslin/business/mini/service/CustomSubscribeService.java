package com.zslin.business.mini.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.mini.dao.ISubscribeMessageDao;
import com.zslin.business.mini.model.SubscribeMessage;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.business.mini.dao.ICustomSubscribeDao;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.business.mini.model.CustomSubscribe;
import com.zslin.core.dto.WxCustomDto;
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
 * Created by 钟述林 on 2020-04-05.
 */
@Service
@AdminAuth(name = "客户消息订阅管理", psn = "移动端管理", orderNum = 2, type = "1", url = "/admin/customSubscribe")
@Explain(name = "客户消息订阅管理", notes = "客户消息订阅管理")
public class CustomSubscribeService {

    @Autowired
    private ICustomSubscribeDao customSubscribeDao;

    @Autowired
    private ISubscribeMessageDao subscribeMessageDao;

    /** 授权订阅消息 */
    public JsonResult accept(String params) {
        try {
            WxCustomDto customDto = JsonTools.getCustom(params);
            String tempId = JsonTools.getJsonParam(params, "tempId");
            SubscribeMessage sm = subscribeMessageDao.findByTempId(tempId);
            if(sm!=null) {
                CustomSubscribe cs = customSubscribeDao.findByCustomOpenidAndMessageId(customDto.getOpenid(), sm.getId());
                if(cs==null) {
                    cs = new CustomSubscribe();
                }
                cs.setCustomId(customDto.getCustomId());
                cs.setCustomNickname(customDto.getNickname());
                cs.setCustomOpenid(customDto.getOpenid());
                cs.setMessageId(sm.getId());
                cs.setMessageName(sm.getName());
                cs.setMessageSn(sm.getSn());
                cs.setStatus("1");
                customSubscribeDao.save(cs);
            }
            return JsonResult.success("保存成功");
        } catch (Exception e) {
//            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "客户消息订阅列表", orderNum = 1)
    @ExplainOperation(name = "客户消息订阅列表", notes = "客户消息订阅列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "客户消息订阅数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "客户消息订阅数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<CustomSubscribe> res = customSubscribeDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "获取客户消息订阅", orderNum = 5)
    @ExplainOperation(name = "获取客户消息订阅信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "客户消息订阅ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            CustomSubscribe obj = customSubscribeDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }


}
