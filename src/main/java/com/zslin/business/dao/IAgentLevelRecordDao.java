package com.zslin.business.dao;

import com.zslin.business.model.AgentLevelRecord;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IAgentLevelRecordDao extends BaseRepository<AgentLevelRecord, Integer>, JpaSpecificationExecutor<AgentLevelRecord> {

}
