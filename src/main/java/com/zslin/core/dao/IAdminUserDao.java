package com.zslin.core.dao;

import com.zslin.core.model.AdminUser;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IAdminUserDao extends BaseRepository<AdminUser, Integer>, JpaSpecificationExecutor<AdminUser> {

    AdminUser findByUsername(String username);

    @Query("SELECT ur.rid FROM UserRole ur WHERE ur.uid=?1")
    List<Integer> listUserRoleIds(Integer userId);

    @Query("UPDATE AdminUser u SET u.phone=?2 WHERE u.username=?1")
    @Modifying
    @Transactional
    void updatePhone(String username, String phone);
}
