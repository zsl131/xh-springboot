package com.zslin.business.sms.dao;

import com.zslin.business.sms.model.SendRecord;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 393156105@qq.com on 2017/2/14 10:19.
 */
public interface ISendRecordDao extends BaseRepository<SendRecord, Integer>, JpaSpecificationExecutor<SendRecord> {
}
