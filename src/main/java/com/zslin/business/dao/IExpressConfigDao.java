package com.zslin.business.dao;

import com.zslin.business.model.ExpressConfig;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2020-04-12.
 */
public interface IExpressConfigDao extends BaseRepository<ExpressConfig, Integer>, JpaSpecificationExecutor<ExpressConfig> {

    @Query("FROM ExpressConfig")
    ExpressConfig loadOne();
}
