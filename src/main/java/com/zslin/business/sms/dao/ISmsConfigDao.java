package com.zslin.business.sms.dao;

import com.zslin.business.sms.model.SmsConfig;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by zsl on 2018/9/25.
 */
public interface ISmsConfigDao extends BaseRepository<SmsConfig, Integer> {

    @Query("FROM SmsConfig")
    SmsConfig loadOne();
}
