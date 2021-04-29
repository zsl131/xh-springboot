package com.zslin.core.dao;

import com.zslin.core.model.RoleMenu;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zsl-pc on 2016/9/1.
 */
public interface IRoleMenuDao extends BaseRepository<RoleMenu, Integer> {

    @Query("SELECT rm.mid FROM RoleMenu rm WHERE rm.rid=:roleId")
    List<Integer> queryMenuIds(@Param("roleId") Integer roleId);

    RoleMenu queryByRidAndMid(Integer rid, Integer mid);

    @Modifying
    @Transactional
    @Query("DELETE RoleMenu rm WHERE rm.mid=?1")
    void deleteByMenuId(Integer menuId);
}
