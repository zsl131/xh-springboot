package com.zslin.business.dao;

import com.zslin.business.model.AgentPaper;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by 钟述林 on 2020-01-01.
 */
public interface IAgentPaperDao extends BaseRepository<AgentPaper, Integer>, JpaSpecificationExecutor<AgentPaper> {

    List<AgentPaper> findByAgentId(Integer agentId);

    AgentPaper findByAgentIdAndFileName(Integer agentId, String fileName);
}
