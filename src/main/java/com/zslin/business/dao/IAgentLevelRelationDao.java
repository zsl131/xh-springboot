package com.zslin.business.dao;

import com.zslin.business.model.AgentLevelRelation;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IAgentLevelRelationDao extends BaseRepository<AgentLevelRelation, Integer>, JpaSpecificationExecutor<AgentLevelRelation> {

    /** 通过代理ID获取等级ID */
    @Query("SELECT a.levelId FROM AgentLevelRelation a WHERE a.agentId=?1")
    Integer findLevelIdByAgentId(Integer agentId);
}
