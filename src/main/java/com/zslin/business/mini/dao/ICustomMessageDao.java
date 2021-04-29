package com.zslin.business.mini.dao;

import com.zslin.business.mini.model.CustomMessage;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-05-06.
 */
public interface ICustomMessageDao extends BaseRepository<CustomMessage, Integer>, JpaSpecificationExecutor<CustomMessage> {

}
