package com.zslin.business.settlement.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.settlement.dao.IRewardRuleDao;
import com.zslin.business.settlement.model.RewardRule;
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
import com.zslin.core.tools.MyBeanUtils;
import com.zslin.core.tools.QueryTools;
import com.zslin.core.validate.ValidationDto;
import com.zslin.core.validate.ValidationTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2020-04-24.
 */
@Service
@AdminAuth(name = "提成奖励金规则管理", psn = "结算管理", orderNum = 2, type = "1", url = "/admin/settlement/rewardRule")
@Explain(name = "提成奖励金规则管理", notes = "提成奖励金规则管理")
public class RewardRuleService {

    @Autowired
    private IRewardRuleDao rewardRuleDao;

    @AdminAuth(name = "提成奖励金规则列表", orderNum = 1)
    @ExplainOperation(name = "提成奖励金规则列表", notes = "提成奖励金规则列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "提成奖励金规则数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "提成奖励金规则数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<RewardRule> res = rewardRuleDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "添加修改奖励金规则", orderNum = 2)
    @ExplainOperation(name = "添加修改奖励金规则", notes = "添加修改奖励金规则", params = {
            @ExplainParam(value = "id", name = "奖励金规则id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "保存成功的对象信息")
    })
    @Transactional
    public JsonResult addOrUpdate(String params) {
        RewardRule obj = JSONObject.toJavaObject(JSON.parseObject(params), RewardRule.class);
        ValidationDto vd = ValidationTools.buildValidate(obj);
        //System.out.println(vd);
        if(vd.isHasError()) { //如果有验证异常
            return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
        }

        RewardRule old = rewardRuleDao.loadOne();
        if(old==null) {
            rewardRuleDao.save(obj);
        } else {
            MyBeanUtils.copyProperties(obj, old);
            rewardRuleDao.save(old);
        }
        return JsonResult.succ(obj);
    }

    @AdminAuth(name = "获取提成奖励金规则", orderNum = 5)
    @ExplainOperation(name = "获取提成奖励金规则信息", notes = "通过ID获取角色对象", back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
//            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
//            RewardRule obj = rewardRuleDao.findOne(id);
            RewardRule obj = rewardRuleDao.loadOne();
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }


}
