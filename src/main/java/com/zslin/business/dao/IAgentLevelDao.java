package com.zslin.business.dao;

import com.zslin.business.model.AgentLevel;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IAgentLevelDao extends BaseRepository<AgentLevel, Integer>, JpaSpecificationExecutor<AgentLevel> {

    /** 通过代理ID获取代理等级对象 */
    @Query("SELECT l FROM AgentLevel l, AgentLevelRelation r WHERE l.id=r.levelId AND r.agentId=?1")
    AgentLevel findByAgentId(Integer agentId);

    @Query("SELECT al FROM AgentLevel al WHERE al.level=(SELECT MIN(a.level) FROM AgentLevel a )")
    AgentLevel queryMinLevel();

    AgentLevel findByLevel(Integer level);
}
