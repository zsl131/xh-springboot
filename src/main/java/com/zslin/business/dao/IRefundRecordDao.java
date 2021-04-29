package com.zslin.business.dao;

import com.zslin.business.model.RefundRecord;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2020-07-06.
 */
public interface IRefundRecordDao extends BaseRepository<RefundRecord, Integer>, JpaSpecificationExecutor<RefundRecord> {

//    @Query("SELECT r.refundNo FROM RefundRecord r WHERE r.ordersNo=?1 AND r.status='-1' ORDER BY r.id DESC LIMIT 0,1")
    @Query("SELECT r FROM RefundRecord r WHERE r.id=(SELECT MAX(rr.id) FROM RefundRecord rr WHERE rr.ordersNo=?1)")
    RefundRecord queryRefundRecord(String ordersNo);
}
