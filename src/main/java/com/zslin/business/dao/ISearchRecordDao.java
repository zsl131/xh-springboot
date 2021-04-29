package com.zslin.business.dao;

import com.zslin.business.model.SearchRecord;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface ISearchRecordDao extends BaseRepository<SearchRecord, Integer>, JpaSpecificationExecutor<SearchRecord> {

    SearchRecord findByKeywordAndCustomId(String keyword, Integer customId);

    @Query("DELETE FROM SearchRecord s WHERE s.customId=?1")
    @Modifying
    @Transactional
    void cleanRecord(Integer customId);
}
