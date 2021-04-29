package com.zslin.business.mini.dao;

import com.zslin.business.mini.model.MiniConfig;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2019-12-01.
 */
public interface IMiniConfigDao extends BaseRepository<MiniConfig, Integer>, JpaSpecificationExecutor<MiniConfig> {

    @Query("FROM MiniConfig m ")
    MiniConfig loadOne();
}
