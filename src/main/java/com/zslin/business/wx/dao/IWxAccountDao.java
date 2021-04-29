package com.zslin.business.wx.dao;

import com.zslin.business.wx.model.WxAccount;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2020-04-14.
 */
public interface IWxAccountDao extends BaseRepository<WxAccount, Integer>, JpaSpecificationExecutor<WxAccount> {

    WxAccount findByOpenid(String openid);

    @Query("UPDATE WxAccount a SET a.type=?1 WHERE a.id=?2")
    @Modifying
    @Transactional
    void updateType(String type, Integer id);

    @Query("UPDATE WxAccount a SET a.status=?2 WHERE a.id=?1")
    @Modifying
    @Transactional
    void updateStatus(Integer id, String status);

    @Query("UPDATE WxAccount a SET a.status=?2 WHERE a.openid=?1")
    @Modifying
    @Transactional
    void updateStatus(String openid, String status);

    @Query("SELECT a.openid FROM WxAccount a WHERE a.type=?1")
    List<String> findOpenid(String type);

    @Query("SELECT COUNT(id) FROM WxAccount ")
    Integer findAllCount();

    @Query("SELECT COUNT(id) FROM WxAccount a WHERE a.status='1'")
    Integer findFollowCount();

    @Query("SELECT COUNT(id) FROM WxAccount a WHERE a.createDay=?1 AND a.status='1'")
    Integer findFollowCountByDay(String day);
}
