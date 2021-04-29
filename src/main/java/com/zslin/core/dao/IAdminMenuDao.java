package com.zslin.core.dao;

import com.zslin.core.model.AdminMenu;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zsl-pc on 2016/9/1.
 */
public interface IAdminMenuDao extends BaseRepository<AdminMenu, Integer>, JpaSpecificationExecutor<AdminMenu> {

//    @Query("SELECT m FROM Menu m WHERE m.display=1 AND m.type='2' AND m.id in (SELECT rm.mid FROM RoleMenu rm WHERE rm.rid IN (SELECT ur.rid FROM UserRole ur where ur.uid=?1))")
    //上面少了 AND m.type='2' 多了 AND m.href!='#'
    @Query("SELECT m FROM AdminMenu m WHERE m.display=1 AND m.href!='#' AND m.id in (SELECT rm.mid FROM RoleMenu rm WHERE rm.rid IN (SELECT ur.rid FROM UserRole ur where ur.uid=?1))")
    List<AdminMenu> findAuthMenuByUser(Integer userId, Sort sort);

    @Query("SELECT m FROM AdminMenu m WHERE m.display=1 AND m.type='1' AND m.id in (SELECT rm.mid FROM RoleMenu rm WHERE rm.rid IN (SELECT ur.rid FROM UserRole ur where ur.uid=?1))")
    List<AdminMenu> findByUser(Integer userId);

    @Query("SELECT m FROM AdminMenu m WHERE m.display=1 AND m.type='1' AND m.psn=?2 AND m.id in (SELECT rm.mid FROM RoleMenu rm WHERE rm.rid IN (SELECT ur.rid FROM UserRole ur where ur.uid=?1)) ")
    List<AdminMenu> findByUser(Integer userId, String psn, Sort sort);

    @Query("SELECT m FROM AdminMenu m WHERE m.display=1 AND m.type='1' AND m.psn IS NULL AND m.id in (SELECT rm.mid FROM RoleMenu rm WHERE rm.rid IN (SELECT ur.rid FROM UserRole ur where ur.uid=?1)) ")
    List<AdminMenu> findRootByUser(Integer userId, Sort sort);

    @Query("FROM AdminMenu m WHERE m.display=1 AND m.pid IS NULL")
    List<AdminMenu> findRootMenu(Sort sort);

    @Query("FROM AdminMenu m WHERE m.display=1 AND m.pid=?1")
    List<AdminMenu> findByParent(Integer pid, Sort sort);

    AdminMenu findBySn(String sn);

    @Query("SELECT m.sn FROM AdminMenu m WHERE m.display=1 AND m.id in (SELECT rm.mid FROM RoleMenu rm WHERE rm.rid IN (SELECT ur.rid FROM UserRole ur where ur.uid=?1))")
    List<String> listAuthByUser(Integer userId);

    @Query("FROM AdminMenu m WHERE m.href is not null AND m.href!='' AND m.href!='#' ")
    List<AdminMenu> listAllUrlMenu();

    @Query("SELECT id from AdminMenu")
    List<Integer> findAllIds();

    @Query("UPDATE AdminMenu m SET m.orderNo=?1 WHERE m.id=?2 ")
    @Modifying
    @Transactional
    void updateOrderNo(Integer orderNo, Integer id);
}
