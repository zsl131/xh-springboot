package com.zslin.business.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.dao.IProductDao;
import com.zslin.business.model.Product;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.rabbit.RabbitNormalTools;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
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
@AdminAuth(name = "产品信息管理", psn = "产品管理", orderNum = 2, type = "1", url = "/admin/product")
@Explain(name = "产品信息管理", notes = "产品信息管理")
public class ProductService {

    @Autowired
    private IProductDao productDao;

    @Autowired
    private RabbitNormalTools rabbitNormalTools;

    @AdminAuth(name = "产品信息列表", orderNum = 1)
    @ExplainOperation(name = "产品信息列表", notes = "产品信息列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "产品信息数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "产品信息数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<Product> res = productDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "添加产品信息", orderNum = 2)
    @ExplainOperation(name = "添加产品信息", notes = "添加产品信息信息", params = {
            @ExplainParam(value = "id", name = "产品信息id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "添加成功的对象信息")
    })
    @Transactional
    public JsonResult add(String params) {
        try {
            Product obj = JSONObject.toJavaObject(JSON.parseObject(params), Product.class);
            ValidationDto vd = ValidationTools.buildValidate(obj);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }

            obj.setCreateDay(NormalTools.curDate());
            obj.setCreateTime(NormalTools.curDatetime());
            obj.setCreateLong(System.currentTimeMillis());
            obj.setUpdateDay(NormalTools.curDate());
            obj.setUpdateTime(NormalTools.curDatetime());
            obj.setUpdateLong(System.currentTimeMillis());

            productDao.save(obj);
            return JsonResult.succ(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @ExplainOperation(name = "修改产品状态", notes = "修改产品状态", params = {
            @ExplainParam(value = "id", name = "产品信息id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "status", name = "状态标识", type = "String", example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "结果标识")
    })
    @Transactional
    public JsonResult modifyStatus(String params) {
        Integer id = JsonTools.getId(params);
        String status = JsonTools.getJsonParam(params, "status");
        String message = "设置成功";
        String flag = "1";
        if("1".equals(status)) { //如果是设置为显示，则需要进行一些判断
            Product pro = productDao.findOne(id);
            if(pro.getSurplusCount()==null || pro.getSurplusCount()<=0) {message = "库存数必须大于0"; flag = "0";}
            if(pro.getPicCount()==null || pro.getPicCount()<=0) {message = "必须先上传图片信息"; flag = "0";}
        }
        if("1".equals(flag)) {productDao.updateStatus(status, id);}
        return JsonResult.success(message).set("flag", flag);
    }

    /** 设置产品是否推荐 */
    public JsonResult modifyRecommend(String params) {
        try {
            Integer id = JsonTools.getId(params);
            String flag = JsonTools.getJsonParam(params, "flag");
            productDao.updateRecommend(flag, id);
            return JsonResult.success("操作成功");
        } catch (Exception e) {
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "修改产品信息", orderNum = 3)
    @ExplainOperation(name = "修改产品信息", notes = "修改产品信息信息", params = {
            @ExplainParam(value = "id", name = "产品信息id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "对应的对象信息")
    })
    @Transactional
    public JsonResult update(String params) {
        try {
            Product o = JSONObject.toJavaObject(JSON.parseObject(params), Product.class);
            ValidationDto vd = ValidationTools.buildValidate(o);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            Product obj = productDao.findOne(o.getId());
//            MyBeanUtils.copyProperties(o, obj, "id", "createDate", "createTime", "createLong", "createDay");
            obj.setTitle(o.getTitle());
            obj.setProvinceCode(o.getProvinceCode());
            obj.setProvinceName(o.getProvinceName());
            obj.setCityCode(o.getCityCode());
            obj.setCityName(o.getCityName());
            obj.setCountyCode(o.getCountyCode());
            obj.setCountyName(o.getCountyName());
            obj.setCateId(o.getCateId());
            obj.setCateName(o.getCateName());
            obj.setPcateId(o.getPcateId());
            obj.setPcateName(o.getPcateName());
            obj.setContent(o.getContent());
            obj.setRawContent(o.getRawContent());
            obj.setFund(o.getFund());
            obj.setSurplusCount(o.getSurplusCount());
            obj.setOrderNo(o.getOrderNo());

            obj.setUpdateDay(NormalTools.curDate());
            obj.setUpdateTime(NormalTools.curDatetime());
            obj.setUpdateLong(System.currentTimeMillis());

            productDao.save(obj);
            onUpdateProduct(obj);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    //更新产品关联更新
    private void onUpdateProduct(Product pro) {
        rabbitNormalTools.updateData("productTools", "onUpdateProduct", pro);
    }

    @AdminAuth(name = "获取产品信息", orderNum = 5)
    @ExplainOperation(name = "获取产品信息信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "产品信息ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            Product obj = productDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "删除产品信息", orderNum = 4)
    @ExplainOperation(name = "删除产品信息", notes = "通过ID删除对象", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "删除标识")
    })
    @Transactional
    public JsonResult delete(String params) {
        Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
        Product r = productDao.findOne(id);
        if(r.getPicCount()>0) {throw new BusinessException(BusinessException.Code.HAVE_SUBELEMENT, "有图片信息，不能删除");}
        if(r.getReplyCount()>0) {throw new BusinessException(BusinessException.Code.HAVE_SUBELEMENT, "有评论信息，不能删除");}
        if(r.getVideoCount()>0) {throw new BusinessException(BusinessException.Code.HAVE_SUBELEMENT, "有视频信息，不能删除");}
        if(r.getSaleCount()>0) {throw new BusinessException(BusinessException.Code.HAVE_SUBELEMENT, "有订单信息，不能删除");}
        if(r.getSpecsCount()>0) {throw new BusinessException(BusinessException.Code.HAVE_SUBELEMENT, "有规格信息，不能删除");}
        if(r.getSaleMode()==null || "0".equals(r.getSaleMode())) {throw new BusinessException(BusinessException.Code.HAVE_SUBELEMENT, "先设置销售类型");}
        productDao.delete(r);
        return JsonResult.success("删除成功");
    }

    @ExplainOperation(name = "设置产品销售模式", notes = "设置产品销售模式", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1"),
            @ExplainParam(value = "mode", name = "模式", type = "String", require = true, example = "1"),
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息")
    })
    public JsonResult modifySaleMode(String params) {
        Integer id = JsonTools.getId(params);
        String mode = JsonTools.getJsonParam(params, "mode");
        productDao.updateMode(mode, "", id);
        return JsonResult.success("设置销售模式成功");
    }

    @ExplainOperation(name = "设置产品预售信息", notes = "设置产品预售信息", params = {
            @ExplainParam(value = "id", name = "预售对象ID", type = "int", require = true, example = "1"),
            @ExplainParam(value = "proId", name = "产品对象ID", type = "int", require = true, example = "1"),
            @ExplainParam(value = "proTitle", name = "产品标题", type = "String", require = true, example = "1"),
            @ExplainParam(value = "deliveryDate", name = "预售时间", type = "String", require = true, example = "1"),
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "obj", notes = "对象信息")
    })
    public JsonResult savePresale(String params) {
        Integer proId = JsonTools.getParamInteger(params, "proId");
        String deliveryDate = JsonTools.getJsonParam(params, "deliveryDate");
        productDao.updateMode("2", deliveryDate, proId);
        return JsonResult.success("设置成功");
    }

    @ExplainOperation(name = "修改产品的计量值", notes = "修改产品的计量值", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1"),
            @ExplainParam(value = "amount", name = "计量值", type = "int", require = true, example = "1"),
            @ExplainParam(value = "field", name = "计量属性", example = "picCount"),
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息")
    })
    @Transactional
    public JsonResult plusCount(String params) {
        Integer id = JsonTools.getId(params);
        Integer amount = JsonTools.getParamInteger(params, "amount");
        String field = JsonTools.getJsonParam(params, "field");
        String hql = "UPDATE Product p SET p."+field+"=p."+field+"+"+amount+" WHERE p.id=?1 ";
        //System.out.println("----------------->hql:"+hql);
        productDao.updateByHql(hql, id);
        return JsonResult.success("操作成功");
    }

    @ExplainOperation(name = "通过标题搜索产品", notes = "通过标题搜索产品", params = {
            @ExplainParam(value = "title", name = "产品标题", require = true, example = "苹果"),
    }, back = {
            @ExplainReturn(field = "proList", notes = "结果数组")
    })
    public JsonResult searchByTitle(String params) {
        String title = JsonTools.getJsonParam(params, "title");
        List<Product> list = productDao.searchByTitle(title);
        return JsonResult.success().set("proList", list);
    }
}
