package com.zslin.business.agent.dao;

import com.zslin.business.agent.model.AgentRole;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-08-22.
 */
public interface IAgentRoleDao extends BaseRepository<AgentRole, Integer>, JpaSpecificationExecutor<AgentRole> {

}
