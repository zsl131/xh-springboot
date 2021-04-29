package com.zslin.business.wx.dao;

import com.zslin.business.wx.model.WxMenu;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2020-04-22.
 */
public interface IWxMenuDao extends BaseRepository<WxMenu, Integer>, JpaSpecificationExecutor<WxMenu> {

    @Query("FROM WxMenu m WHERE m.pid IS NULL OR m.pid<=0 ORDER BY m.orderNo ASC")
    List<WxMenu> findParent();

    @Query("FROM WxMenu m WHERE m.pid=?1 ORDER BY m.orderNo ASC")
    List<WxMenu> findByPid(Integer pid);

    @Query("FROM WxMenu c WHERE (c.pid IS NULL OR c.pid = 0) ")
    List<WxMenu> findRoot(Sort sort);

    List<WxMenu> findByPid(Integer pid, Sort sort);

    @Query("FROM WxMenu c WHERE c.status=?1 AND (c.pid IS NULL OR c.pid = 0) ")
    List<WxMenu> findRoot(String status, Sort sort);

    List<WxMenu> findByPidAndStatus(Integer pid, String status, Sort sort);

    /** 获取子元素数量，用于删除分类前判断 */
    @Query("SELECT COUNT(c.id) FROM WxMenu c WHERE c.pid=?1 ")
    Long findCountByPid(Integer pid);

    @Query("UPDATE WxMenu c SET c.orderNo=?1 WHERE c.id=?2 ")
    @Modifying
    @Transactional
    void updateOrderNo(Integer orderNo, Integer id);
}
