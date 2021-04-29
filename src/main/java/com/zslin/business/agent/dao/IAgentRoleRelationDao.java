package com.zslin.business.agent.dao;

import com.zslin.business.agent.model.AgentRoleRelation;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-08-22.
 */
public interface IAgentRoleRelationDao extends BaseRepository<AgentRoleRelation, Integer>, JpaSpecificationExecutor<AgentRoleRelation> {

}
