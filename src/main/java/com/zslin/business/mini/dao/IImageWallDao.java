package com.zslin.business.mini.dao;

import com.zslin.business.mini.model.ImageWall;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2020-03-25.
 */
public interface IImageWallDao extends BaseRepository<ImageWall, Integer>, JpaSpecificationExecutor<ImageWall> {

    @Query("FROM ImageWall w WHERE (w.title IS NULL OR w.title='') AND w.customId=?1")
    List<ImageWall> findNoFinished(Integer customId, Sort sort);

    ImageWall findByIdAndCustomId(Integer id, Integer customId);

    @Query("UPDATE ImageWall w SET w.goodCount=w.goodCount+?1 WHERE w.id=?2")
    @Modifying
    @Transactional
    void plusGoodCount(Integer count, Integer id);

    @Query("UPDATE ImageWall w SET w.status=?1 WHERE w.id=?2")
    @Modifying
    @Transactional
    void modifyStatus(String status, Integer id);
}
