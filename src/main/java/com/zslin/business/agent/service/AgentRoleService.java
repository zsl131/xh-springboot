package com.zslin.business.agent.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.agent.dao.IAgentSpecsDao;
import com.zslin.business.agent.model.AgentSpecs;
import com.zslin.business.dao.IProductCategoryDao;
import com.zslin.business.dao.IProductDao;
import com.zslin.business.dao.IProductSpecsDao;
import com.zslin.business.dto.CategoryTreeDto;
import com.zslin.business.model.*;
import com.zslin.business.tools.CategoryTools;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.business.agent.dao.IAgentRoleDao;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.business.agent.model.AgentRole;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.repository.SpecificationOperator;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import com.zslin.core.validate.ValidationDto;
import com.zslin.core.validate.ValidationTools;
import com.zslin.core.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.zslin.core.tools.MyBeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2020-08-22.
 */
@Service
@AdminAuth(name = "代理角色管理", psn = "代理商管理", orderNum = 2, type = "1", url = "/admin/agentRole")
@Explain(name = "代理角色管理", notes = "代理角色管理")
public class AgentRoleService {

    @Autowired
    private IAgentRoleDao agentRoleDao;

    @Autowired
    private IProductCategoryDao productCategoryDao;

    @Autowired
    private IProductDao productDao;

    @Autowired
    private IProductSpecsDao productSpecsDao;

    @Autowired
    private CategoryTools categoryTools;

    @Autowired
    private IAgentSpecsDao agentSpecsDao;

    @AdminAuth(name = "代理角色列表", orderNum = 1)
    @ExplainOperation(name = "代理角色列表", notes = "代理角色列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "代理角色数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "代理角色数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<AgentRole> res = agentRoleDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "添加代理角色", orderNum = 2)
    @ExplainOperation(name = "添加代理角色", notes = "添加代理角色信息", params = {
            @ExplainParam(value = "id", name = "代理角色id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "添加成功的对象信息")
    })
    @Transactional
    public JsonResult add(String params) {
        try {
            AgentRole obj = JSONObject.toJavaObject(JSON.parseObject(params), AgentRole.class);
            ValidationDto vd = ValidationTools.buildValidate(obj);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            agentRoleDao.save(obj);
            return JsonResult.succ(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "修改代理角色", orderNum = 3)
    @ExplainOperation(name = "修改代理角色", notes = "修改代理角色信息", params = {
            @ExplainParam(value = "id", name = "代理角色id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "对应的对象信息")
    })
    @Transactional
    public JsonResult update(String params) {
        try {
            AgentRole o = JSONObject.toJavaObject(JSON.parseObject(params), AgentRole.class);
            ValidationDto vd = ValidationTools.buildValidate(o);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            AgentRole obj = agentRoleDao.findOne(o.getId());
            MyBeanUtils.copyProperties(o, obj, "id", "createDate", "createTime", "createLong", "createDay");
            agentRoleDao.save(obj);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "获取代理角色", orderNum = 5)
    @ExplainOperation(name = "获取代理角色信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "代理角色ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            AgentRole obj = agentRoleDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "删除代理角色", orderNum = 4)
    @ExplainOperation(name = "删除代理角色", notes = "通过ID删除对象", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "删除标识")
    })
    @Transactional
    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            AgentRole r = agentRoleDao.findOne(id);
            agentRoleDao.delete(r);
            return JsonResult.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    public JsonResult listProduct(String params) {
        Integer pid = 0; String type = "base";
        Integer rid = JsonTools.getParamInteger(params, "roleId"); //当前选择的角色ID

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
            Page<ProductCategory> res = productCategoryDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                    new SpecificationOperator("pid", "eq", pid)),
                    SimplePageBuilder.generate(qld.getPage(), qld.getSize(), sort));
            result.set("data", res.getContent()).set("category", productCategoryDao.findOne(pid)).set("total", res.getTotalElements());
        } else if("detail".equalsIgnoreCase(type)) { //是产品，则获取产品信息
//            System.out.println("---------------333");
            Product pro = productDao.findOne(pid);
            List<ProductSpecs> specsList = productSpecsDao.findByProId(pid, sort);
            List<AgentSpecs> agentSpecsList = agentSpecsDao.findByRoleId(rid);
            result.set("product", pro).set("specsList", specsList).set("agentSpecsList", agentSpecsList);
        } else {
//            System.out.println("---------------444");
            List<ProductCategory> list = productCategoryDao.findRoot(SimpleSortBuilder.generateSort("orderNo"));
            result.set("data", list).set("category", "").set("type", "base");
        }

        List<CategoryTreeDto> treeList = categoryTools.buildTree();
        result.set("treeList", treeList).set("type", type);
        return result;
    }
}
