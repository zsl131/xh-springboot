package com.zslin.business.dao;

import com.zslin.business.model.CashOut;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2020-04-16.
 */
public interface ICashOutDao extends BaseRepository<CashOut, Integer>, JpaSpecificationExecutor<CashOut> {

    /** 获取进行中的提现数据 */
    @Query("FROM CashOut c WHERE c.status='0' AND c.agentId=?1")
    CashOut findByRunningByAgentId(Integer agentId);

    @Query("SELECT SUM(c.money) FROM CashOut c WHERE c.payLong IS NOT NULL AND c.payLong>=?1 AND c.payLong<=?2")
    Double findMoney(Long startPayLong, Long endPayLong);
}
