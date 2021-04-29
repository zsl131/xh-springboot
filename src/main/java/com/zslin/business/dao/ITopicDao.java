package com.zslin.business.dao;

import com.zslin.business.model.Topic;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2020-04-22.
 */
@Component("topicDao")
public interface ITopicDao extends BaseRepository<Topic, Integer>, JpaSpecificationExecutor<Topic> {

    Topic findBySn(String sn);

    @Query("UPDATE Topic t SET t.readCount=t.readCount+?1 WHERE t.id=?2 ")
    @Modifying
    @Transactional
    void plusReadCount(Integer amount, Integer id);
}
