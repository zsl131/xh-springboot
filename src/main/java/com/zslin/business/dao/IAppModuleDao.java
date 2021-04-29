package com.zslin.business.dao;

import com.zslin.business.model.AppModule;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IAppModuleDao extends BaseRepository<AppModule, Integer>, JpaSpecificationExecutor<AppModule> {

    @Query("UPDATE AppModule c SET c.orderNo=?1 WHERE c.id=?2 ")
    @Modifying
    @Transactional
    void updateOrderNo(Integer orderNo, Integer id);

    @Query("UPDATE AppModule a SET a.status=?1 WHERE a.id=?2 ")
    @Modifying
    @Transactional
    void updateStatus(String status, Integer id);
}
