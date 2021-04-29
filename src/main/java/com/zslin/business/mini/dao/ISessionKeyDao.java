package com.zslin.business.mini.dao;

import com.zslin.business.mini.model.SessionKey;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-04-04.
 */
public interface ISessionKeyDao extends BaseRepository<SessionKey, Integer>, JpaSpecificationExecutor<SessionKey> {

    SessionKey findByCustomId(Integer customId);

    SessionKey findByOpenid(String openid);
}
