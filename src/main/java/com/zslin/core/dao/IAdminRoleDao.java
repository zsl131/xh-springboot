package com.zslin.core.dao;

import com.zslin.core.model.AdminRole;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by 钟述林 393156105@qq.com on 2016/10/19 9:59.
 */
public interface IAdminRoleDao extends BaseRepository<AdminRole, Integer>, JpaSpecificationExecutor<AdminRole> {

    @Query("SELECT rm.mid FROM RoleMenu rm WHERE rm.rid=?1")
    List<Integer> listRoleMenuIds(Integer roleId);

    AdminRole findBySn(String sn);
}
