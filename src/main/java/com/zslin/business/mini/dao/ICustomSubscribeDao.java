package com.zslin.business.mini.dao;

import com.zslin.business.mini.model.CustomSubscribe;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-04-05.
 */
public interface ICustomSubscribeDao extends BaseRepository<CustomSubscribe, Integer>, JpaSpecificationExecutor<CustomSubscribe> {

    CustomSubscribe findByCustomOpenidAndMessageId(String customOpenid, Integer messageId);
}
