package com.zslin.business.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.dao.ICarouselDao;
import com.zslin.business.dao.IMediumDao;
import com.zslin.business.model.Carousel;
import com.zslin.business.tools.CarouselTools;
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

/**
 * Created by 钟述林 on 2019-12-18.
 */
@Service
@AdminAuth(name = "轮播图管理", psn = "移动端管理", orderNum = 2, type = "1", url = "/admin/carousel")
@Explain(name = "轮播图管理", notes = "轮播图管理")
public class CarouselService {

    @Autowired
    private ICarouselDao carouselDao;

    @Autowired
    private IMediumDao mediumDao;

    @Autowired
    private CarouselTools carouselTools;

    @AdminAuth(name = "轮播图列表", orderNum = 1)
    @ExplainOperation(name = "轮播图列表", notes = "轮播图列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "轮播图数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "轮播图数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<Carousel> res = carouselDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "添加轮播图", orderNum = 2)
    @ExplainOperation(name = "添加轮播图", notes = "添加轮播图信息", params = {
            @ExplainParam(value = "id", name = "轮播图id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "添加成功的对象信息")
    })
    @Transactional
    public JsonResult add(String params) {
        try {
            Carousel obj = JSONObject.toJavaObject(JSON.parseObject(params), Carousel.class);
            ValidationDto vd = ValidationTools.buildValidate(obj);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            carouselDao.save(obj);

            //修改媒介的归属
            mediumDao.modifyOwn(obj.getId(), "Carousel", obj.getToken());
            return JsonResult.succ(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "修改轮播图", orderNum = 3)
    @ExplainOperation(name = "修改轮播图", notes = "修改轮播图信息", params = {
            @ExplainParam(value = "id", name = "轮播图id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "对应的对象信息")
    })
    @Transactional
    public JsonResult update(String params) {
        try {
            Carousel o = JSONObject.toJavaObject(JSON.parseObject(params), Carousel.class);
            ValidationDto vd = ValidationTools.buildValidate(o);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            Carousel obj = carouselDao.findOne(o.getId());
            MyBeanUtils.copyProperties(o, obj, "id", "createDate", "createTime", "createLong", "createDay", "token");
            carouselDao.save(obj);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "获取轮播图", orderNum = 5)
    @ExplainOperation(name = "获取轮播图信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "轮播图ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            Carousel obj = carouselDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "删除轮播图", orderNum = 4)
    @ExplainOperation(name = "删除轮播图", notes = "通过ID删除对象", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "删除标识")
    })
    @Transactional
    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            Carousel r = carouselDao.findOne(id);
            carouselDao.delete(r);
            return JsonResult.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @ExplainOperation(name = "修改轮播图状态", notes = "修改轮播图状态", params = {
            @ExplainParam(value = "id", name = "对象id", require = true, type = "int", example = "1"),
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
        if("1".equals(flag)) {carouselDao.updateStatus(status, id);}
        return JsonResult.success(message).set("flag", flag);
    }

    @ExplainOperation(name = "初始化轮播图序号", notes = "为每个轮播图生成一个不重复的序号", back = {
            @ExplainReturn(field = "message", notes = "初始化结果信息")
    })
    public JsonResult initOrderNo(String params) {
        carouselTools.buildOrderNo(); //重新生成序号
        return JsonResult.success("初始化成功");
    }
}
