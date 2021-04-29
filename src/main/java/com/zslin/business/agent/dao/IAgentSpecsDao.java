package com.zslin.business.agent.dao;

import com.zslin.business.agent.model.AgentSpecs;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by 钟述林 on 2020-08-22.
 */
public interface IAgentSpecsDao extends BaseRepository<AgentSpecs, Integer>, JpaSpecificationExecutor<AgentSpecs> {

    List<AgentSpecs> findByRoleId(Integer roleId);
}
