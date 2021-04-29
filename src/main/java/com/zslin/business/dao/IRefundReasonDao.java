package com.zslin.business.dao;

import com.zslin.business.model.RefundReason;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-08-04.
 */
public interface IRefundReasonDao extends BaseRepository<RefundReason, Integer>, JpaSpecificationExecutor<RefundReason> {

}
