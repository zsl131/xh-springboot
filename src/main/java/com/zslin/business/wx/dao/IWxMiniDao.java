package com.zslin.business.wx.dao;

import com.zslin.business.wx.model.WxMini;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2020-04-14.
 */
public interface IWxMiniDao extends BaseRepository<WxMini, Integer>, JpaSpecificationExecutor<WxMini> {

    WxMini findByWxOpenid(String wxOpenid);

    WxMini findByMiniOpenid(String miniOpenid);

    @Query("SELECT w.wxOpenid FROM WxMini w WHERE w.miniOpenid=?1 ")
    String queryWxOpenid(String miniOpenid);
}
