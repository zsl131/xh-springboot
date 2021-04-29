package com.zslin.business.dao;

import com.zslin.business.model.AppNotice;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IAppNoticeDao extends BaseRepository<AppNotice, Integer>, JpaSpecificationExecutor<AppNotice> {

    @Query("UPDATE AppNotice a SET a.status=?1 WHERE a.id=?2 ")
    @Modifying
    @Transactional
    void updateStatus(String status, Integer id);
}
