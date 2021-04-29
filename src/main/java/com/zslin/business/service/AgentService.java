package com.zslin.business.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.dao.IAgentDao;
import com.zslin.business.dao.IAgentLevelDao;
import com.zslin.business.dao.IAgentPaperDao;
import com.zslin.business.dao.ICustomerDao;
import com.zslin.business.model.Agent;
import com.zslin.business.model.AgentLevel;
import com.zslin.business.model.AgentPaper;
import com.zslin.business.tools.AgentTools;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.MyBeanUtils;
import com.zslin.core.tools.QueryTools;
import com.zslin.core.validate.ValidationDto;
import com.zslin.core.validate.ValidationTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2020-01-02.
 */
@Service
@AdminAuth(name = "代理管理", psn = "销售管理", orderNum = 2, type = "1", url = "/admin/agent")
@Explain(name = "代理管理", notes = "代理管理")
public class AgentService {

    @Autowired
    private IAgentDao agentDao;

    @Autowired
    private IAgentPaperDao agentPaperDao;

    @Autowired
    private AgentTools agentTools;

    @Autowired
    private IAgentLevelDao agentLevelDao;

    @Autowired
    private ICustomerDao customerDao;

    @ExplainOperation(name = "代理申请审核", notes = "代理申请审核", params= {
            @ExplainParam(value = "id", name = "代理ID", require = true, type = "int", example = "1"),
            @ExplainParam(value = "status", name = "审核状态结果", require = true, type = "String", example = "1"),
            @ExplainParam(value = "reason", name = "审核原因结果", require = true, type = "String")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
    })
    public JsonResult verify(String params) {
        //System.out.println(params);
        Integer id = JsonTools.getId(params);
        String status = JsonTools.getJsonParam(params, "status");
        Integer level = JsonTools.getParamInteger(params, "level");
//        String reason = JsonTools.getJsonParam(params, "reason");
        Agent a = agentDao.findOne(id);

        AgentLevel al = null;
        if(level!=null && level>0) {
            al = agentLevelDao.findOne(level);
        }
        agentTools.verify(params, a, al); //审核过后默认设置为初级代理

        if(al!=null) {
            a.setLevelId(level);
            a.setLevelName(al.getName());
        }

        a.setStatus(status);
        agentDao.save(a); //修改状态

        if("1".equals(status)) { //如果是审核通过，则修改Customer的相关信息
            updateCustomer(a.getName(), a.getPhone(), a.getId(), a.getOpenid());
        }

        agentDao.plusVerifyCount(1, a.getId());
        if("1".equals(status)) { //只有审核通过才进行等级调整
            agentDao.plusRelationCount(1, a.getId());
        }

        return JsonResult.success("操作成功");
    }

    /** 审核通过时修改姓名等信息 */
    private void updateCustomer(String name, String phone, Integer agentId, String openid) {
        customerDao.updateName(name, phone, agentId, openid);
    }

    @ExplainOperation(name = "获取代理资质", notes = "获取代理资质", params= {
            @ExplainParam(value = "id", name = "代理ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "资质数量"),
            @ExplainReturn(field = "data", type = "Object", notes = "资质数组对象")
    })
    public JsonResult listPapers(String params) {
        Integer id = JsonTools.getId(params);
        List<AgentPaper> list = agentPaperDao.findByAgentId(id);
        return JsonResult.success().set("size", list.size()).set("data", list);
    }

    @AdminAuth(name = "代理列表", orderNum = 1)
    @ExplainOperation(name = "代理列表", notes = "代理列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "代理数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "代理数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<Agent> res = agentDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "修改代理", orderNum = 3)
    @ExplainOperation(name = "修改代理", notes = "修改代理信息", params = {
            @ExplainParam(value = "id", name = "代理id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "对应的对象信息")
    })
    @Transactional
    public JsonResult update(String params) {
        try {
            Agent o = JSONObject.toJavaObject(JSON.parseObject(params), Agent.class);
            ValidationDto vd = ValidationTools.buildValidate(o);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            Agent obj = agentDao.findOne(o.getId());
            MyBeanUtils.copyProperties(o, obj, "id", "createDate", "createTime", "createLong", "createDay");
            agentDao.save(obj);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "获取代理", orderNum = 5)
    @ExplainOperation(name = "获取代理信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "代理ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            Agent obj = agentDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "删除代理", orderNum = 4)
    @ExplainOperation(name = "删除代理", notes = "通过ID删除对象", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "删除标识")
    })
    @Transactional
    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            Agent r = agentDao.findOne(id);
            agentDao.delete(r);
            return JsonResult.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }


}
