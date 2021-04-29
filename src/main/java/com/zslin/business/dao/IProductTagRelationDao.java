package com.zslin.business.dao;

import com.zslin.business.model.ProductTagRelation;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IProductTagRelationDao extends BaseRepository<ProductTagRelation, Integer>, JpaSpecificationExecutor<ProductTagRelation> {

}
