package com.zslin.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.dao.IAdminRoleDao;
import com.zslin.core.dao.IAdminUserDao;
import com.zslin.core.dao.IUserRoleDao;
import com.zslin.core.dto.AuthRoleDto;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.LoginUserDto;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.model.AdminRole;
import com.zslin.core.model.AdminUser;
import com.zslin.core.model.UserRole;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.*;
import com.zslin.core.tools.login.LoginDto;
import com.zslin.core.tools.login.LoginTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zsl on 2018/7/10.
 */
@Service(value = "adminUserService")
@AdminAuth(psn = "系统管理", name = "用户管理", orderNum = 1, type = "1", url = "/admin/basic/users")
@Explain(name = "用户管理", notes = "后台用户管理", value = "adminUserService")
public class AdminUserService {

    @Autowired
    private IAdminUserDao adminUserDao;

    @Autowired
    private IAdminRoleDao adminRoleDao;

    @Autowired
    private IUserRoleDao userRoleDao;

    @Autowired
    private AuthTokenTools authTokenTools;

    @Autowired
    private LoginTools loginTools;

    @AdminAuth(name = "用户列表", orderNum = 1)
    @ExplainOperation(name = "用户列表", value = "listUser", notes = "用户列表", params= {
            @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
            @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
            @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
            @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
    }, back = {
            @ExplainReturn(field = "size", notes = "数据数量"),
            @ExplainReturn(field = "datas", notes = "对象列表")
    })
    public JsonResult listUser(String params) {
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<AdminUser> res = adminUserDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
    }

    @ExplainOperation(name = "获取用户信息", notes = "通过用户ID获取用户对象", params = {
            @ExplainParam(value = "id", name = "用户ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", notes = "对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            AdminUser user = adminUserDao.findOne(id);
            return JsonResult.succ(user);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "添加或修改用户", orderNum = 2)
    @ExplainOperation(name = "添加或修改修改用户", notes = "添加或修改用户信息，有id则添加", params = {
            @ExplainParam(value = "id", name = "用户id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他用户信息", require = false, type = "Object", example = "对应用户数据")
    }, back = {
            @ExplainReturn(field = "obj", notes = "对象信息"),
    })
    @Transactional
    public JsonResult saveUser(String params) {
//        System.out.println("params::   "+ params);
        try {
            AdminUser u = JSONObject.toJavaObject(JSON.parseObject(params), AdminUser.class);
            if(u.getId()==null || u.getId()<=0) { //添加
                AdminUser user = adminUserDao.findByUsername(u.getUsername());
                if(user!=null) {
//                    return JsonResult.error(u.getUsername()+"已经存在");
                    return JsonResult.getInstance().failFlag(u.getUsername()+"已经存在");
                }
                u.setPassword(SecurityUtil.md5(u.getUsername(), u.getPassword()));
                u.setCreateDate(DateTools.currentDay("yyyy-MM-dd HH:mm:ss"));
                adminUserDao.save(u);
            } else {
                AdminUser user = adminUserDao.getOne(u.getId());
                if(u.getPassword()!=null && !"".equals(u.getPassword())) {
                    user.setPassword(SecurityUtil.md5(u.getUsername(), u.getPassword()));
                }
                user.setStatus(u.getStatus());
                user.setNickname(u.getNickname());
                adminUserDao.save(user);
            }
            return JsonResult.succ(u);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "删除用户", orderNum = 3)
    @ExplainOperation(name = "删除用户", notes = "通过用户ID删除用户，不能删除系统管理员", params = {
            @ExplainParam(value = "id", name = "用户ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "删除标识")
    })
    @Transactional
    public JsonResult deleteUser(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            AdminUser u = adminUserDao.getOne(id);
            if("1".equalsIgnoreCase(u.getIsAdmin())) {
//                return JsonResult.error("管理员用户不可以删除");
                return JsonResult.getInstance().failFlag("管理员用户不可以删除");
            } else {
                adminUserDao.delete(u);
                return JsonResult.success("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @ExplainOperation(name = "用户登陆", notes = "通过用户名和密码登陆用户", params = {
            @ExplainParam(value = "username", name = "用户名", type = "String", require = true, example = "admin"),
            @ExplainParam(value = "password", name = "密码", type = "String", require = true, example = "111111")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "标识")
    })
    @NeedAuth(need = false)
    public JsonResult login(String params) {
        try {
            String username = JsonTools.getJsonParam(params, "username");
            String password = JsonTools.getJsonParam(params, "password");
            if(username == null || "".equals(username) || password == null || "".equals(password)) {
//                return JsonResult.error("用户名或密码为空");
                return JsonResult.getInstance().failFlag("用户名或密码为空");
            }
            AdminUser user = adminUserDao.findByUsername(username);
            if(user==null || !"1".equals(user.getStatus())) {
//                return JsonResult.error("用户不存在或被停用");
                return JsonResult.getInstance().failFlag("用户不存在或被停用");
            }
            password = SecurityUtil.md5(username, password);
            if(!password.equals(user.getPassword())) {
//                return JsonResult.error("密码不正确");
                return JsonResult.getInstance().failFlag("密码不正确");
            }

            LoginDto loginDto = loginTools.buildAuthMenus(user.getId());
            LoginUserDto userDto = new LoginUserDto(user.getId(), user.getUsername(), user.getNickname(),user.getPhone());
            loginDto.setUser(user);
            loginDto.setToken(authTokenTools.buildToken(userDto));

            return JsonResult.succ(loginDto);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @ExplainOperation(name = "获取用户权限", notes = "通过用户ID获取对应的角色数据和权限ID", params = {
            @ExplainParam(value = "id", name = "用户id", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "obj", notes = "角色数组")
    })
    public JsonResult matchRole(String params) {
        try {
            Integer userId = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            List<Integer> roleIds = adminUserDao.listUserRoleIds(userId); //已拥有的角色Id
            List<AdminRole> roleList = adminRoleDao.findAll();
            AuthRoleDto ard = new AuthRoleDto(roleIds, roleList);
            return JsonResult.succ(ard);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "分配用户角色", orderNum = 5)
    @ExplainOperation(name = "分配用户角色", notes = "为用户分配角色", params = {
            @ExplainParam(value = "uid", name = "用户ID", type = "int", require = true, example = "1"),
            @ExplainParam(value = "rids", name = "角色ID数组", type = "Array", require = true, example = "[1,2,3]")
    }, back = {
            @ExplainReturn(field = "message", notes = "结果信息")
    })
    public JsonResult authRole(String params) {
        System.out.println(params);
        try {
            Integer uid = Integer.parseInt(JsonTools.getJsonParam(params, "uid"));
            userRoleDao.deleteByUserId(uid); //先删除所有权限
            String rids = JsonTools.getJsonParam(params, "rids");
            JSONArray array = JSON.parseArray(rids);
            for(Integer i=0;i<array.size();i++) {
                Integer rid = array.getInteger(i);
                UserRole ur = new UserRole();
                ur.setUid(uid);
                ur.setRid(rid);
                userRoleDao.save(ur);
            }
            return JsonResult.success("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @ExplainOperation(name = "修改密码", notes = "修改用户密码", params = {
            @ExplainParam(value = "id", name = "用户ID", type = "int", require = true, example = "1"),
            @ExplainParam(value = "oldPwd", name = "原始密码", type = "String", require = true, example = "111111"),
            @ExplainParam(value = "password", name = "设置的新密码", type = "String", require = true, example = "123456"),
            @ExplainParam(value = "nickname", name = "用户昵称", type = "String", require = false, example = "")
    }, back = {
            @ExplainReturn(field = "message", notes = "结果信息")
    })
    public JsonResult updatePwd(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            AdminUser user = adminUserDao.getOne(id);
            String username = user.getUsername();
            String oldPwd = JsonTools.getJsonParam(params, "oldPwd");
            String password = JsonTools.getJsonParam(params, "password");
            String nickname = JsonTools.getJsonParam(params, "nickname");
            if(!SecurityUtil.md5(username, oldPwd).equals(user.getPassword())) {
//                return JsonResult.error("原始密码输入错误");
                return JsonResult.getInstance().failFlag("原始密码输入错误");
            }
            if(nickname!=null && !"".equals(nickname.trim())) {
                user.setNickname(nickname);
            }
            user.setPassword(SecurityUtil.md5(username, password));
            adminUserDao.save(user);
            return JsonResult.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }
}
