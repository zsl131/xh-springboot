package com.zslin.business.wx.dao;

import com.zslin.business.wx.model.WxConfig;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2020-04-14.
 */
public interface IWxConfigDao extends BaseRepository<WxConfig, Integer>, JpaSpecificationExecutor<WxConfig> {

    @Query("FROM WxConfig w ")
    WxConfig loadOne();
}
