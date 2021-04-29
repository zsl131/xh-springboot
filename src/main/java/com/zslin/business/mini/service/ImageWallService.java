package com.zslin.business.mini.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.mini.dao.ICustomImageRelationDao;
import com.zslin.business.mini.model.CustomImageRelation;
import com.zslin.business.model.CustomCommissionRecord;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.business.mini.dao.IImageWallDao;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.business.mini.model.ImageWall;
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
 * Created by 钟述林 on 2020-03-25.
 */
@Service
@AdminAuth(name = "影像墙管理", psn = "销售管理", orderNum = 2, type = "1", url = "/admin/imageWall")
@Explain(name = "影像墙管理", notes = "影像墙管理")
public class ImageWallService {

    @Autowired
    private IImageWallDao imageWallDao;

    @Autowired
    private ICustomImageRelationDao customImageRelationDao;

    @ExplainOperation(name = "设置用户的影像墙权限", notes = "设置用户的影像墙权限", params= {
            @ExplainParam(value = "id", name = "用户ID", require = true, type = "int", example = "1"),
            @ExplainParam(value = "type", name = "类型", require = true, type = "String", example = "1")
    }, back = {
            @ExplainReturn(field = "message", type = "String", notes = "操作结果")
    })
    public JsonResult updateType(String params) {
        try {
            Integer id = JsonTools.getId(params);
            String type = JsonTools.getJsonParam(params, "type");

            CustomImageRelation cir = customImageRelationDao.findByCustomId(id);
            if(cir==null) {
                cir = new CustomImageRelation();
            }
            cir.setCustomId(id);
            cir.setType(type);
            customImageRelationDao.save(cir);
            return JsonResult.success("操作成功");
        } catch (Exception e) {
            return JsonResult.error(e.getMessage());
        }
    }

    @ExplainOperation(name = "获取用户的影像墙权限", notes = "获取用户的影像墙权限", params= {
            @ExplainParam(value = "id", name = "用户ID", require = true, type = "int", example = "1"),
    }, back = {
            @ExplainReturn(field = "type", type = "String", notes = "类型，0-不可上传；1-可上传，需审核；2-可上传，无需审核")
    })
    public JsonResult loadType(String params) {
        Integer id = JsonTools.getId(params);
        String type = customImageRelationDao.findType(id);
        return JsonResult.success("获取成功").set("type", type);
    }

    @AdminAuth(name = "影像墙列表", orderNum = 1)
    @ExplainOperation(name = "影像墙列表", notes = "影像墙列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "影像墙数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "影像墙数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<ImageWall> res = imageWallDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "修改影像墙", orderNum = 3)
    @ExplainOperation(name = "修改影像墙", notes = "修改影像墙信息", params = {
            @ExplainParam(value = "id", name = "影像墙id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "对应的对象信息")
    })
    @Transactional
    public JsonResult update(String params) {
        try {
            ImageWall o = JSONObject.toJavaObject(JSON.parseObject(params), ImageWall.class);
            ValidationDto vd = ValidationTools.buildValidate(o);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            ImageWall obj = imageWallDao.findOne(o.getId());
            MyBeanUtils.copyProperties(o, obj, "id", "createDate", "createTime", "createLong", "createDay");
            imageWallDao.save(obj);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "获取影像墙", orderNum = 5)
    @ExplainOperation(name = "获取影像墙信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "影像墙ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            ImageWall obj = imageWallDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    public JsonResult modifyStatus(String params) {
        Integer id = JsonTools.getId(params);
        String status = JsonTools.getJsonParam(params, "status");
        imageWallDao.modifyStatus(status, id);
        return JsonResult.success("操作成功");
    }

    public JsonResult relationProduct(String params) {
        try {
            Integer id = JsonTools.getId(params);
            String title = JsonTools.getJsonParam(params, "proTitle");
            Integer proId = JsonTools.getParamInteger(params, "proId");
            ImageWall wall = imageWallDao.findOne(id);
            wall.setRelationProId(proId);
            wall.setRelationProTitle(title);
            wall.setRelationFlag("1");
            imageWallDao.save(wall);
            return JsonResult.success("设置成功");
        } catch (Exception e) {
            return JsonResult.error(e.getMessage());
        }
    }
}
