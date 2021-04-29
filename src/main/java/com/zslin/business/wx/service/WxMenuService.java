package com.zslin.business.wx.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.wx.dao.IWxMenuDao;
import com.zslin.business.wx.dto.WxMenuDto;
import com.zslin.business.wx.model.WxMenu;
import com.zslin.business.wx.tools.WxMenuTools;
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
import com.zslin.core.repository.SpecificationOperator;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.MyBeanUtils;
import com.zslin.core.tools.QueryTools;
import com.zslin.core.validate.ValidationDto;
import com.zslin.core.validate.ValidationTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2020-04-22.
 */
@Service
@AdminAuth(name = "微信菜单管理", psn = "微信管理", orderNum = 2, type = "1", url = "/admin/wx/wxMenu")
@Explain(name = "微信菜单管理", notes = "微信菜单管理")
public class WxMenuService {

    @Autowired
    private IWxMenuDao wxMenuDao;
    
    @Autowired
    private WxMenuTools wxMenuTools;

    /** 发布菜单 */
    public JsonResult publishMenu(String params) {
        wxMenuTools.publishMenu();
        return JsonResult.success("发布成功");
    }

    @ExplainOperation(name = "初始化序号", notes = "为每个微信菜单生成一个不重复的序号", back = {
            @ExplainReturn(field = "message", notes = "初始化结果信息")
    })
    public JsonResult initOrderNo(String params) {
        wxMenuTools.buildWxMenuOrderNo(); //重新生成序号
        return JsonResult.success("初始化成功");
    }

    @AdminAuth(name = "微信菜单列表", orderNum = 1)
    @ExplainOperation(name = "构建分类树", notes = "获取子分类列表", params = {
            @ExplainParam(name = "父ID", value = "pid", type = "int", example = "1，不传则获取根分类")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "数据数量"),
            @ExplainReturn(field = "datas", type = "Object", notes = "数据列表")
    })
    public JsonResult listRoot(String params) {
        Integer pid = 0; String type = "base";

        try {
            String pidStr = JsonTools.getJsonParam(params, "pid"); //root_33
            String [] array = pidStr.split("_");
            type = array[0];
            pid = Integer.parseInt(array[1]);
        } catch (Exception e) {
        }

        type = ("0".equals(type)?"base":type);
        //System.out.println("------>"+type+"-------"+pid);

        JsonResult result = JsonResult.getInstance();
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");

        if("root".equalsIgnoreCase(type)) { //是根分类，则获取子分类
//            System.out.println("---------------111");
//            List<Category> children = categoryDao.findByPid(pid, sort);
            QueryListDto qld = QueryTools.buildQueryListDto(params);
            Page<WxMenu> res = wxMenuDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                    new SpecificationOperator("pid", "eq", pid)),
                    SimplePageBuilder.generate(qld.getPage(), qld.getSize(), sort));
            result.set("data", res.getContent()).set("menu", wxMenuDao.findOne(pid)).set("total", res.getTotalElements());
        } else if("child".equalsIgnoreCase(type)) { //是子分类，则获取产品列表
//            System.out.println("---------------222");
            QueryListDto qld = QueryTools.buildQueryListDto(params);
            Page<WxMenu> res = wxMenuDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                    new SpecificationOperator("pid", "eq", pid)),
                    SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));
            result.set("proList", res.getContent()).set("menu", wxMenuDao.findOne(pid)).set("total", res.getTotalElements());
        } else {
//            System.out.println("---------------444");
            List<WxMenu> list = wxMenuDao.findRoot(SimpleSortBuilder.generateSort("orderNo"));
            result.set("data", list).set("menu", "").set("type", "base");
        }

        List<WxMenuDto> treeList = wxMenuTools.buildTree();
        result.set("treeList", treeList).set("type", type);
        return result;
    }

    @ExplainOperation(name = "微信菜单列表", notes = "获取子分类列表", params = {
            @ExplainParam(name = "父ID", value = "pid", type = "int", example = "1，不传则获取根分类")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "数据数量"),
            @ExplainReturn(field = "datas", type = "Object", notes = "数据列表")
    })
    public JsonResult listChildren(String params) {
        Integer pid = 0;
        try { pid = Integer.parseInt(JsonTools.getJsonParam(params, "pid"));} catch (Exception e) {pid=0;}
        Sort sort = SimpleSortBuilder.generateSort("orderNum_a");
        List<WxMenu> categoryList ;
        if(pid==0) {
            categoryList = wxMenuDao.findRoot(sort);
        } else {
            categoryList = wxMenuDao.findByPid(pid, sort);
        }

        return JsonResult.getInstance().set("size", categoryList.size()).set("datas", categoryList);
    }

    @AdminAuth(name = "微信菜单列表", orderNum = 1)
    @ExplainOperation(name = "微信菜单列表", notes = "微信菜单列表", params= {
            @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
            @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
            @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
            @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "微信菜单数量"),
            @ExplainReturn(field = "datas", type = "Object", notes = "微信菜单数组对象")
    })
    public JsonResult list(String params) {
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<WxMenu> res = wxMenuDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
    }

    @AdminAuth(name = "添加微信菜单", orderNum = 2)
    @ExplainOperation(name = "添加微信菜单", notes = "添加微信菜单信息", params = {
            @ExplainParam(value = "id", name = "微信菜单id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "添加成功的对象信息")
    })
    @Transactional
    public JsonResult add(String params) {
        try {
            WxMenu obj = JSONObject.toJavaObject(JSON.parseObject(params), WxMenu.class);
            ValidationDto vd = ValidationTools.buildValidate(obj);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            wxMenuDao.save(obj);
            return JsonResult.succ(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "修改微信菜单", orderNum = 3)
    @ExplainOperation(name = "修改微信菜单", notes = "修改微信菜单信息", params = {
            @ExplainParam(value = "id", name = "微信菜单id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "对应的对象信息")
    })
    @Transactional
    public JsonResult update(String params) {
        try {
            WxMenu o = JSONObject.toJavaObject(JSON.parseObject(params), WxMenu.class);
            ValidationDto vd = ValidationTools.buildValidate(o);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            WxMenu obj = wxMenuDao.findOne(o.getId());
            MyBeanUtils.copyProperties(o, obj, "id", "createDate", "createTime", "createLong", "createDay");
            wxMenuDao.save(obj);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "获取微信菜单", orderNum = 5)
    @ExplainOperation(name = "获取微信菜单信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "微信菜单ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            WxMenu obj = wxMenuDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "删除微信菜单", orderNum = 4)
    @ExplainOperation(name = "删除微信菜单", notes = "通过ID删除对象", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "删除标识")
    })
    @Transactional
    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            if(wxMenuDao.findCountByPid(id)>0) {
                throw new BusinessException(BusinessException.Code.HAVE_SUBELEMENT, BusinessException.Message.HAVE_SUBELEMENT);
            }
            WxMenu r = wxMenuDao.findOne(id);
            wxMenuDao.delete(r);
            return JsonResult.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }
}
