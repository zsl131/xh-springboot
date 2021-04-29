package com.zslin.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.dao.IAdminMenuDao;
import com.zslin.core.dao.IRoleMenuDao;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.dto.TreeRootDto;
import com.zslin.core.model.AdminMenu;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.*;
import com.zslin.core.tools.login.MenuTreeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zsl on 2018/7/13.
 */
@Service
@AdminAuth(name = "菜单管理", psn = "系统管理", orderNum = 3, type = "1", url = "/admin/basic/menu")
@Explain(name = "系统菜单管理", notes = "系统菜单管理")
public class AdminMenuService {

    @Autowired
    private IAdminMenuDao menuDao;

    @Autowired
    private MenuTools menuTools;

    @Autowired
    private IRoleMenuDao roleMenuDao;

    @Autowired
    private BuildAdminMenuTools buildAdminMenuTools;

    @Autowired
    private AuthRoleMenuTools authRoleMenuTools;

    @ExplainOperation(name = "初始化菜单序号", notes = "为每个菜单生成一个不重复的序号", back = {
            @ExplainReturn(field = "message", notes = "初始化结果信息")
    })
    public JsonResult initOrderNo(String params) {
        buildAdminMenuTools.buildAdminMenusOrderNo(); //重新生成序号
        return JsonResult.success("初始化成功");
    }

    @AdminAuth(name = "初始化菜单", orderNum = 6)
    @ExplainOperation(name = "初始化系统菜单", notes = "当系统中增加功能是可以通过此功能增加相应功能菜单", back = {
            @ExplainReturn(field = "message", notes = "初始化结果信息")
    })
    @Transactional
    public JsonResult init(String params) {
        buildAdminMenuTools.buildAdminMenus(); //重构菜单
        authRoleMenuTools.authAdmin(); //为系统管理员授权菜单
        return JsonResult.getInstance("初始化菜单成功");
    }

    @AdminAuth(name = "菜单列表", orderNum = 1)
    @ExplainOperation(name = "构建菜单树", notes = "获取子菜单列表", params = {
            @ExplainParam(name = "父ID", value = "pid", type = "int", example = "1，不传则获取根菜单")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "菜单数量"),
            @ExplainReturn(field = "datas", type = "Object", notes = "菜单列表")
    })
    public JsonResult listRoot(String params) {
        Integer pid = 0;
        try { pid = Integer.parseInt(JsonTools.getJsonParam(params, "pid"));} catch (Exception e) {pid=0;}
        List<MenuTreeDto> list = menuTools.buildMenuTree();
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<AdminMenu> menuList ;
        if(pid==0) {
            menuList = menuDao.findRootMenu(sort);
        } else {
            menuList = menuDao.findByParent(pid, sort);
        }
        TreeRootDto trd = new TreeRootDto(list, menuList);
        return JsonResult.getInstance().set("size", menuList.size()).set("datas", trd);
    }

    /**
     *
     * @param params {pid: 1}
     * @return
     */
    @ExplainOperation(name = "菜单列表", notes = "获取子菜单列表", params = {
            @ExplainParam(name = "父ID", value = "pid", type = "int", example = "1，不传则获取根菜单")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "菜单数量"),
            @ExplainReturn(field = "datas", type = "Object", notes = "菜单列表")
    })
    public JsonResult listChildren(String params) {
        Integer pid = 0;
        try { pid = Integer.parseInt(JsonTools.getJsonParam(params, "pid"));} catch (Exception e) {pid=0;}
        Sort sort = SimpleSortBuilder.generateSort("orderNum_a");
        List<AdminMenu> menuList ;
        if(pid==0) {
            menuList = menuDao.findRootMenu(sort);
        } else {
            menuList = menuDao.findByParent(pid, sort);
        }

        return JsonResult.getInstance().set("size", menuList.size()).set("datas", menuList);
    }

    @ExplainOperation(name = "菜单列表", notes = "菜单列表", params= {
            @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
            @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
            @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
            @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
    }, back = {
            @ExplainReturn(field = "size", notes = "数据数量"),
            @ExplainReturn(field = "datas", notes = "对象列表")
    })
    public JsonResult list(String params) {
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<AdminMenu> res = menuDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
    }

    @AdminAuth(name = "添加菜单", orderNum = 2)
    @ExplainOperation(name = "添加菜单", notes = "添加菜单信息", params = {
            @ExplainParam(value = "id", name = "对象id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "datas", type = "Object", notes = "添加成功的对象信息")
    })
    @Transactional
    public JsonResult add(String params) {
        try {
            AdminMenu menu = JSONObject.toJavaObject(JSON.parseObject(params), AdminMenu.class);
            menuDao.save(menu);
            return JsonResult.getInstance().set("datas", menu);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "修改菜单", orderNum = 3)
    @ExplainOperation(name = "修改菜单信息", notes = "通过对象ID获取对象", params = {
            @ExplainParam(value = "id", name = "对象ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "datas", notes = "对象信息")
    })
    @Transactional
    public JsonResult update(String params) {
        try {
            AdminMenu menu = JSONObject.toJavaObject(JSON.parseObject(params), AdminMenu.class);
            AdminMenu m = menuDao.findOne(menu.getId());
            m.setOrderNo(menu.getOrderNo());
            m.setHref(menu.getHref());
            m.setName(menu.getName());
            m.setIcon(menu.getIcon());
            menuDao.save(m);
            return JsonResult.getInstance().set("datas", m);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "删除菜单", orderNum = 4)
    @ExplainOperation(name = "删除菜单", notes = "通过对象ID删除对象，不建议操作此功能", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息")
    })
    @Transactional
    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            menuDao.deleteById(id);
            roleMenuDao.deleteByMenuId(id);
            return JsonResult.getInstance("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }
}
