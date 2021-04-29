package com.zslin.core.tools;

import com.zslin.core.dao.IAdminMenuDao;
import com.zslin.core.dao.IAdminRoleDao;
import com.zslin.core.dao.IRoleMenuDao;
import com.zslin.core.model.AdminRole;
import com.zslin.core.model.RoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zsl on 2018/7/23.
 */
@Component
public class AuthRoleMenuTools {

    @Autowired
    private IAdminRoleDao roleDao;

    @Autowired
    private IRoleMenuDao roleMenuDao;

    @Autowired
    private IAdminMenuDao menuDao;

    public void authAdmin() {
        AdminRole r = roleDao.findBySn("SYSTEM_ADMIN");
        if(r!=null) {
            Integer rid = r.getId();
            List<Integer> alreadyMenuIds = roleMenuDao.queryMenuIds(rid);
            List<Integer> allMenuIds = menuDao.findAllIds();
            for(Integer id : allMenuIds) {
                if(!alreadyMenuIds.contains(id)) {
                    RoleMenu rm = new RoleMenu();
                    rm.setMid(id);
                    rm.setRid(rid);
                    roleMenuDao.save(rm);
                }
            }
        }
    }
}
