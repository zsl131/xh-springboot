package com.zslin.business.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.dao.ICouponDao;
import com.zslin.business.dao.ICouponRuleDetailDao;
import com.zslin.business.model.Coupon;
import com.zslin.business.model.CouponRuleDetail;
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
 * Created by 钟述林 on 2019-12-18.
 */
@Service
@AdminAuth(name = "优惠券管理", psn = "销售管理", orderNum = 2, type = "1", url = "/admin/coupon")
@Explain(name = "优惠券管理", notes = "优惠券管理")
public class CouponService {

    @Autowired
    private ICouponDao couponDao;

    @Autowired
    private ICouponRuleDetailDao couponRuleDetailDao;

    @AdminAuth(name = "优惠券列表", orderNum = 1)
    @ExplainOperation(name = "优惠券列表", notes = "优惠券列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "优惠券数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "优惠券数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<Coupon> res = couponDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "添加优惠券", orderNum = 2)
    @ExplainOperation(name = "添加优惠券", notes = "添加优惠券信息", params = {
            @ExplainParam(value = "id", name = "优惠券id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "添加成功的对象信息")
    })
    @Transactional
    public JsonResult add(String params) {
        try {
            Coupon obj = JSONObject.toJavaObject(JSON.parseObject(params), Coupon.class);
            ValidationDto vd = ValidationTools.buildValidate(obj);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            couponDao.save(obj);
            return JsonResult.succ(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "修改优惠券", orderNum = 3)
    @ExplainOperation(name = "修改优惠券", notes = "修改优惠券信息", params = {
            @ExplainParam(value = "id", name = "优惠券id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "对应的对象信息")
    })
    @Transactional
    public JsonResult update(String params) {
        try {
            Coupon o = JSONObject.toJavaObject(JSON.parseObject(params), Coupon.class);
            ValidationDto vd = ValidationTools.buildValidate(o);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            Coupon obj = couponDao.findOne(o.getId());
            MyBeanUtils.copyProperties(o, obj, "id", "createDate", "createTime", "createLong", "createDay", "receiveCount");
            couponDao.save(obj);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "获取优惠券", orderNum = 5)
    @ExplainOperation(name = "获取优惠券信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "优惠券ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            Coupon obj = couponDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "删除优惠券", orderNum = 4)
    @ExplainOperation(name = "删除优惠券", notes = "通过ID删除对象", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "删除标识")
    })
    @Transactional
    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            Coupon r = couponDao.findOne(id);
            couponDao.delete(r);
            return JsonResult.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @ExplainOperation(name = "修改状态", notes = "修改状态", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1"),
            @ExplainParam(value = "status", name = "状态标识", require = true)
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
    })
    public JsonResult updateStatus(String params) {
        try {
            Integer id = JsonTools.getId(params);
            String status = JsonTools.getJsonParam(params, "status");
            couponDao.updateByHql("UPDATE Coupon c SET c.status=?2 WHERE c.id=?1", id, status);
            return JsonResult.success("设置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error("操作失败", e.getMessage());
        }
    }

    @ExplainOperation(name = "修改叠加属性", notes = "修改叠加属性", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1"),
            @ExplainParam(value = "canRepeat", name = "叠加标识", require = true)
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
    })
    public JsonResult updateRepeat(String params) {
        try {
            Integer id = JsonTools.getId(params);
            String canRepeat = JsonTools.getJsonParam(params, "canRepeat");
            couponDao.updateByHql("UPDATE Coupon c SET c.canRepeat=?2 WHERE c.id=?1", id, canRepeat);
            return JsonResult.success("设置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error("操作失败", e.getMessage());
        }
    }

    @ExplainOperation(name = "通过名称查询数据", notes = "通过名称查询数据", params = {
            @ExplainParam(value = "name", name = "名称", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "couponList", notes = "结果数组"),
    })
    public JsonResult searchByName(String params) {
        String name = JsonTools.getJsonParam(params, "name");
        List<Coupon> list = couponDao.searchByName(name);
        return JsonResult.success().set("couponList", list);
    }

    /** 获取所有抵价券，用于规则分配 */
    public JsonResult queryCoupon(String params) {
        Integer ruleId = JsonTools.getId(params); //RuleId
        List<Coupon> couponList = couponDao.findAll();
        //已经分配的抵价券ID
        List<Integer> reallyIds = couponRuleDetailDao.queryIds(ruleId);
        return JsonResult.success("获取成功").set("couponList", couponList).set("couponIds", reallyIds);
    }

    /** 授权规则对应的抵价券 */
    public JsonResult authCoupon(String params) {
        Integer ruleId = JsonTools.getParamInteger(params, "ruleId");
        String ruleSn = JsonTools.getJsonParam(params, "ruleSn");
//        String flag = JsonTools.getJsonParam(params, "flag");

        couponRuleDetailDao.deleteByRuleId(ruleId); //先删除所有权限
        String cids = JsonTools.getJsonParam(params, "cids"); //CouponIds
        JSONArray array = JSON.parseArray(cids);
        for(Integer i=0;i<array.size();i++) {
            Integer cid = array.getInteger(i);
            CouponRuleDetail cr = new CouponRuleDetail();
            cr.setCouponId(cid);
            cr.setRuleId(ruleId);
            cr.setRuleSn(ruleSn);
            couponRuleDetailDao.save(cr);
        }

        return JsonResult.success("操作成功");
    }
}
