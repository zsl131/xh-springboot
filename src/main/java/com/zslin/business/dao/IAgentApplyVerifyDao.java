package com.zslin.business.dao;

import com.zslin.business.model.AgentApplyVerify;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IAgentApplyVerifyDao extends BaseRepository<AgentApplyVerify, Integer>, JpaSpecificationExecutor<AgentApplyVerify> {

    List<AgentApplyVerify> findByOpenid(String openid, Sort sort);
}
