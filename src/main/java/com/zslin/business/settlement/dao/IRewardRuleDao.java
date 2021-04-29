package com.zslin.business.settlement.dao;

import com.zslin.business.settlement.model.RewardRule;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2020-04-24.
 */
public interface IRewardRuleDao extends BaseRepository<RewardRule, Integer>, JpaSpecificationExecutor<RewardRule> {

    @Query("FROM RewardRule r ")
    RewardRule loadOne();
}
