package com.zslin.business.wx.dao;

import com.zslin.business.wx.model.Feedback;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2020-04-14.
 */
public interface IFeedbackDao extends BaseRepository<Feedback, Integer>, JpaSpecificationExecutor<Feedback> {

    @Query("UPDATE Feedback f SET f.status=?2 WHERE f.id=?1")
    @Modifying
    @Transactional
    void updateStatus(Integer id, String status);
}
