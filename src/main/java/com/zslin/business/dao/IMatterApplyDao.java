package com.zslin.business.dao;

import com.zslin.business.model.MatterApply;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2020-05-11.
 */
public interface IMatterApplyDao extends BaseRepository<MatterApply, Integer>, JpaSpecificationExecutor<MatterApply> {

    List<MatterApply> findByCustomId(Integer customId);

    @Query("UPDATE MatterApply m SET m.status=?1 WHERE m.id=?2")
    @Modifying
    @Transactional
    void updateStatus(String status, Integer id);
}
