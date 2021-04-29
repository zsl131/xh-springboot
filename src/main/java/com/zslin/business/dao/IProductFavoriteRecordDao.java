package com.zslin.business.dao;

import com.zslin.business.model.ProductFavoriteRecord;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2020-01-31.
 */
public interface IProductFavoriteRecordDao extends BaseRepository<ProductFavoriteRecord, Integer>, JpaSpecificationExecutor<ProductFavoriteRecord> {

    ProductFavoriteRecord findByProIdAndCustomId(Integer proId, Integer customId);

    @Modifying
    @Transactional
    void deleteByIdAndCustomId(Integer id, Integer customId);

    @Query("SELECT p.id FROM ProductFavoriteRecord p WHERE p.customId=?1")
    List<Integer> findIdsByCustomId(Integer customId);

    @Query("UPDATE ProductFavoriteRecord p SET p.nickname=?1 WHERE p.customId=?2")
    @Modifying
    @Transactional
    void updateNickname(String nickname, Integer customId);
}
