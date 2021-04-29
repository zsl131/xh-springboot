package com.zslin.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.dao.IAdminMenuDao;
import com.zslin.core.dao.IAdminRoleDao;
import com.zslin.core.dao.IRoleMenuDao;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.dto.TreeRootDto;
import com.zslin.core.model.AdminMenu;
import com.zslin.core.model.AdminRole;
import com.zslin.core.model.RoleMenu;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.MenuTools;
import com.zslin.core.tools.PinyinToolkit;
import com.zslin.core.tools.QueryTools;
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
@AdminAuth(name = "角色管理", psn = "系统管理", orderNum = 2, type = "1", url = "/admin/basic/role")
@Explain(name = "系统角色管理", notes = "系统角色管理")
public class AdminRoleService {

    @Autowired
    private IAdminRoleDao adminRoleDao;

    @Autowired
    private IRoleMenuDao roleMenuDao;

    @Autowired
    private IAdminMenuDao menuDao;

    @Autowired
    private MenuTools menuTools;

    @ExplainOperation(name = "权限角色菜单", notes = "为角色授权菜单，存在则取消授权，不存在则添加授权", params = {
            @ExplainParam(value = "rid", name = "角色ID", type = "int", require = true, example = "1"),
            @ExplainParam(value = "mid", name = "菜单ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "授权结果信息")
    })
    public JsonResult authMenu(String params) {
        try {
            Integer mid = Integer.parseInt(JsonTools.getJsonParam(params, "mid"));
            Integer rid = Integer.parseInt(JsonTools.getJsonParam(params, "rid"));
            RoleMenu rm = roleMenuDao.queryByRidAndMid(rid, mid);
            String message ;
            if(rm==null) {
                rm = new RoleMenu();
                rm.setMid(mid);
                rm.setRid(rid);
                roleMenuDao.save(rm);
                message = "授权成功";
            } else {
                roleMenuDao.delete(rm);
                message = "取消授权成功";
            }
            return JsonResult.getInstance(message);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @ExplainOperation(name = "获取角色菜单ID", notes = "获取角色已有权限菜单的ID列表", params = {
            @ExplainParam(value = "rid", name = "角色ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "菜单数量"),
            @ExplainReturn(field = "datas", type = "Object", notes = "菜单ID数组")
    })
    public JsonResult listRoleMenuIds(String params) {
        try {
            Integer rid = Integer.parseInt(JsonTools.getJsonParam(params, "rid"));
            List<Integer> mids = roleMenuDao.queryMenuIds(rid);
            return JsonResult.getInstance().set("size", mids.size()).set("datas", mids);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "菜单列表", orderNum = 1)
    @ExplainOperation(name = "构建菜单树", notes = "获取子菜单列表", params = {
            @ExplainParam(name = "父ID", value = "pid", type = "int", example = "1，不传则获取根菜单")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "菜单数量"),
            @ExplainReturn(field = "mids", type = "Array", notes = "已拥有的菜单ID"),
            @ExplainReturn(field = "tree", type = "Object", notes = "菜单树对象")
    })
    public JsonResult onAuth(String params) {
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
        Integer rid = Integer.parseInt(JsonTools.getJsonParam(params, "rid"));
        List<Integer> mids = roleMenuDao.queryMenuIds(rid);

        return JsonResult.getInstance().set("size", mids.size()).set("mids", mids).set("tree", trd);
    }

    @AdminAuth(name = "角色列表", orderNum = 1)
    @ExplainOperation(name = "角色列表", notes = "角色列表", params= {
            @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
            @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
            @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
            @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "角色数量"),
            @ExplainReturn(field = "datas", type = "Object", notes = "角色数组对象")
    })
    public JsonResult list(String params) {
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<AdminRole> res = adminRoleDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
    }

    @AdminAuth(name = "添加角色", orderNum = 2)
    @ExplainOperation(name = "添加角色", notes = "添加角色信息", params = {
            @ExplainParam(value = "id", name = "对象id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "添加成功的对象信息")
    })
    @Transactional
    public JsonResult add(String params) {
        try {
            AdminRole role = JSONObject.toJavaObject(JSON.parseObject(params), AdminRole.class);
            String sn = PinyinToolkit.cn2Spell(role.getName(),"").toUpperCase();
            if(adminRoleDao.findBySn(sn)!=null) {
//                return JsonResult.getInstance().fail("【"+sn+"】已经存在");
                return JsonResult.getInstance().failFlag("【"+sn+"】已经存在");
            }
            role.setSn(sn);
            adminRoleDao.save(role);
            return JsonResult.succ(role);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "修改角色", orderNum = 3)
    @ExplainOperation(name = "修改角色", notes = "修改角色信息", params = {
            @ExplainParam(value = "id", name = "对象id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "对应的对象信息")
    })
    @Transactional
    public JsonResult update(String params) {
        try {
            AdminRole r = JSONObject.toJavaObject(JSON.parseObject(params), AdminRole.class);
            r.setSn(PinyinToolkit.cn2Spell(r.getName(),"").toUpperCase());
            adminRoleDao.save(r);
            return JsonResult.getInstance().set("obj", r);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @ExplainOperation(name = "获取角色信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "对象ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            AdminRole role = adminRoleDao.findOne(id);
            return JsonResult.getInstance().set("obj", role);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "删除角色", orderNum = 4)
    @ExplainOperation(name = "删除角色", notes = "通过ID删除对象，不能删除系统管理员角色", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "删除标识")
    })
    @Transactional
    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            AdminRole r = adminRoleDao.findOne(id);
            if("SYSTEM_ADMIN".equalsIgnoreCase(r.getSn())) {
//                return JsonResult.error("系统管理员角色不可以删除");
                return JsonResult.getInstance().failFlag("系统管理员角色不可以删除");
            } else {
                adminRoleDao.delete(r);
                return JsonResult.success("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }
}
