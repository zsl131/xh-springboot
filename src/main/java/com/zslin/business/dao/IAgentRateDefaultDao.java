package com.zslin.business.dao;

import com.zslin.business.model.AgentRateDefault;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2019-12-28.
 */
public interface IAgentRateDefaultDao extends BaseRepository<AgentRateDefault, Integer>, JpaSpecificationExecutor<AgentRateDefault> {

    AgentRateDefault findByLevelId(Integer levelId);

    @Query("FROM AgentRateDefault a WHERE a.levelId=?1")
    AgentRateDefault getRate(Integer levelId);
}
