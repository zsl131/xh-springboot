package com.zslin.core.dao;

import com.zslin.core.model.BaseAppConfig;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by zsl-pc on 2016/9/7.
 */
public interface IBaseAppConfigDao extends BaseRepository<BaseAppConfig, Integer> {

    @Query("FROM BaseAppConfig ")
    BaseAppConfig loadOne();

    @Override
    <S extends BaseAppConfig> S save(S s);
}
