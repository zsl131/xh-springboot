package com.zslin.business.mini.dao;

import com.zslin.business.mini.model.SubscribeMessage;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-04-05.
 */
public interface ISubscribeMessageDao extends BaseRepository<SubscribeMessage, Integer>, JpaSpecificationExecutor<SubscribeMessage> {

    SubscribeMessage findBySn(String sn);

    SubscribeMessage findByTempId(String tempId);
}
