package com.zslin.business.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.dao.IProductDao;
import com.zslin.business.dao.IProductSpecsDao;
import com.zslin.business.model.ProductSpecs;
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
@Explain(name = "产品规格管理", notes = "产品规格管理")
public class ProductSpecsService {

    @Autowired
    private IProductSpecsDao productSpecsDao;

    @Autowired
    private IProductDao productDao;

    @AdminAuth(name = "产品规格列表", orderNum = 1)
    @ExplainOperation(name = "产品规格列表", notes = "产品规格列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "产品规格数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "产品规格数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<ProductSpecs> res = productSpecsDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @ExplainOperation(name = "通过产品获取规格", notes = "通过产品获取规格", params= {
            @ExplainParam(value = "proId", name = "产品ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "产品规格数量"),
            @ExplainReturn(field = "data", type = "Object", notes = "产品规格数组对象")
    })
     public JsonResult listByProduct(String params) {
        try {
            Integer proId = JsonTools.getParamInteger(params, "proId");
            List<ProductSpecs> list = productSpecsDao.findByProId(proId, SimpleSortBuilder.generateSort("orderNo_a"));
            return JsonResult.success("获取成功").set("size", list.size()).set("data", list);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(BusinessException.Code.DEFAULT_ERR_CODE, e.getMessage());
        }
    }

    @AdminAuth(name = "添加或修改产品规格", orderNum = 2)
    @ExplainOperation(name = "添加或修改产品规格", notes = "添加或修改产品规格", params = {
            @ExplainParam(value = "id", name = "产品规格id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "添加成功的对象信息")
    })
    @Transactional
    public JsonResult save(String params) {
        try {
            ProductSpecs obj = JSONObject.toJavaObject(JSON.parseObject(params), ProductSpecs.class);
            ValidationDto vd = ValidationTools.buildValidate(obj);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }

            if(obj.getId()!=null && obj.getId()>0) { //修改
                ProductSpecs o = productSpecsDao.findOne(obj.getId());
                MyBeanUtils.copyProperties(obj, o, "id", "cateId", "proId");
                productSpecsDao.save(o);
                updateProductPrice(o.getProId());
                return JsonResult.succ(o);
            } else {
                obj = productSpecsDao.save(obj);
                productDao.plusSpecsCount(1, obj.getProId()); //修改规格数
                updateProductPrice(obj.getProId());
                return JsonResult.succ(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    /** 修改产品库存 */
    private void modifyProductAmount(Integer proId) {
        Integer amount = productSpecsDao.queryProductTotalAmount(proId);
        productDao.updateSurplus(amount, proId);
    }

    /**
     * 设置产品显示的价格
     * @param proId
     */
    private void updateProductPrice(Integer proId) {
        Float price = productSpecsDao.queryPrice(proId);
        productDao.updatePrice(price, proId);

        modifyProductAmount(proId); //修改库存
    }

    @AdminAuth(name = "获取产品规格", orderNum = 5)
    @ExplainOperation(name = "获取产品规格信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "产品规格ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            ProductSpecs obj = productSpecsDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "删除产品规格", orderNum = 4)
    @ExplainOperation(name = "删除产品规格", notes = "通过ID删除对象", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "删除标识")
    })
    @Transactional
    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            ProductSpecs r = productSpecsDao.findOne(id);
            productDao.plusSpecsCount(-1, r.getProId());
            productSpecsDao.delete(r);
            return JsonResult.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }


}
