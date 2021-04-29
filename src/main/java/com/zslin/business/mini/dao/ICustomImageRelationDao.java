package com.zslin.business.mini.dao;

import com.zslin.business.mini.model.CustomImageRelation;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2020-03-25.
 */
public interface ICustomImageRelationDao extends BaseRepository<CustomImageRelation, Integer>, JpaSpecificationExecutor<CustomImageRelation> {

    @Query("SELECT c.type FROM CustomImageRelation c WHERE c.customId=?1")
    String findType(Integer customId);

    CustomImageRelation findByCustomId(Integer customId);
}
