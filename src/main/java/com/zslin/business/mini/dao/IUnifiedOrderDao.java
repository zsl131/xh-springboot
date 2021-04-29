package com.zslin.business.mini.dao;

import com.zslin.business.mini.model.UnifiedOrder;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-04-07.
 */
public interface IUnifiedOrderDao extends BaseRepository<UnifiedOrder, Integer>, JpaSpecificationExecutor<UnifiedOrder> {

}
