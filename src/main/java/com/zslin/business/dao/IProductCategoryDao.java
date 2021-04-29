package com.zslin.business.dao;

import com.zslin.business.model.ProductCategory;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IProductCategoryDao extends BaseRepository<ProductCategory, Integer>, JpaSpecificationExecutor<ProductCategory> {

    ProductCategory findBySn(String sn);

    @Query("FROM ProductCategory c WHERE (c.pid IS NULL OR c.pid = 0) ")
    List<ProductCategory> findRoot(Sort sort);

    List<ProductCategory> findByPid(Integer pid, Sort sort);

    /** 获取子元素数量，用于删除分类前判断 */
    @Query("SELECT COUNT(c.id) FROM ProductCategory c WHERE c.pid=?1 ")
    Long findCountByPid(Integer pid);

    @Query("UPDATE ProductCategory c SET c.orderNo=?1 WHERE c.id=?2 ")
    @Modifying
    @Transactional
    void updateOrderNo(Integer orderNo, Integer id);
}
