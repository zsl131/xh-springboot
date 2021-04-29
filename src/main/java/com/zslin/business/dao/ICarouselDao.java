package com.zslin.business.dao;

import com.zslin.business.model.Carousel;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface ICarouselDao extends BaseRepository<Carousel, Integer>, JpaSpecificationExecutor<Carousel> {

    @Query("UPDATE Carousel c SET c.orderNo=?1 WHERE c.id=?2 ")
    @Modifying
    @Transactional
    void updateOrderNo(Integer orderNo, Integer id);

    @Query("UPDATE Carousel a SET a.status=?1 WHERE a.id=?2 ")
    @Modifying
    @Transactional
    void updateStatus(String status, Integer id);
}
