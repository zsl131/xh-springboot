package com.zslin.business.dao;

import com.zslin.business.model.ProductHeadImg;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IProductHeadImgDao extends BaseRepository<ProductHeadImg, Integer>, JpaSpecificationExecutor<ProductHeadImg> {

}
