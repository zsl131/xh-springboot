package com.zslin.business.settlement.dao;

import com.zslin.business.settlement.model.ReceiptRecord;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2020-04-24.
 */
public interface IReceiptRecordDao extends BaseRepository<ReceiptRecord, Integer>, JpaSpecificationExecutor<ReceiptRecord> {

    /** 获取代理是否已经领取奖金 */
    @Query("SELECT COUNT(r.id) FROM ReceiptRecord r WHERE r.rewardProduceMonth=?1 AND r.createMonth=?2 AND r.customOpenid=?3")
    Long queryCount(String rewardProduceMonth, String createMonth, String customOpenid);
}
