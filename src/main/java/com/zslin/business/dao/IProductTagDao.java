package com.zslin.business.dao;

import com.zslin.business.model.ProductTag;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IProductTagDao extends BaseRepository<ProductTag, Integer>, JpaSpecificationExecutor<ProductTag> {

    @Query("UPDATE ProductTag c SET c.orderNo=?1 WHERE c.id=?2 ")
    @Modifying
    @Transactional
    void updateOrderNo(Integer orderNo, Integer id);

    @Query("UPDATE ProductTag a SET a.status=?1 WHERE a.id=?2 ")
    @Modifying
    @Transactional
    void updateStatus(String status, Integer id);

    @Query("SELECT MAX(p.orderNo) FROM ProductTag p ")
    Integer maxOrderNo();

    List<ProductTag> findByName(String name);
}
