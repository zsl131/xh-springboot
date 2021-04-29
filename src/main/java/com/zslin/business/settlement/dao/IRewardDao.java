package com.zslin.business.settlement.dao;

import com.zslin.business.settlement.dto.AgentRewardDto;
import com.zslin.business.settlement.model.Reward;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2020-04-24.
 */
public interface IRewardDao extends BaseRepository<Reward, Integer>, JpaSpecificationExecutor<Reward> {

    /** 获取是否已经添加 */
    @Query("SELECT COUNT(r.id) FROM Reward r WHERE r.produceMonth=?1")
    Long queryCount(String month);

    /** 获取个人的单月奖金记录 */
    Reward findByProduceMonthAndCustomOpenid(String month, String openid);

    //Double totalExtraMoney, Double totalReceiptMoney, Double totalSurplusMoney
    @Query("SELECT new com.zslin.business.settlement.dto.AgentRewardDto(SUM(r.extraMoney), SUM(r.receiptMoney), SUM(r.surplusMoney)) " +
            "FROM Reward r WHERE r.customOpenid=?1")
    AgentRewardDto queryDto(String agentOpenid);
}
