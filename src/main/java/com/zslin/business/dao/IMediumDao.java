package com.zslin.business.dao;

import com.zslin.business.model.Medium;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-08.
 */
public interface IMediumDao extends BaseRepository<Medium, Integer>, JpaSpecificationExecutor<Medium> {

    /** 更新归属 */
    @Query("UPDATE Medium m SET m.objId=?1 WHERE m.objClassName=?2 AND m.ticket=?3 ")
    @Modifying
    @Transactional
    Integer modifyOwn(Integer objId, String objType, String ticket);

    List<Medium> findByObjClassNameAndObjId(String objClassName, Integer objId, Sort sort);

    List<Medium> findByObjClassNameAndObjIdAndStatus(String objClassName, Integer objId, String status, Sort sort);

    Medium findByTicket(String ticket);

    @Query("UPDATE Medium m SET m.status=?1 WHERE m.id=?2")
    @Modifying
    @Transactional
    void updateStatus(String status, Integer id);

    List<Medium> findByObjIdAndObjClassNameAndIsFirst(Integer objId, String objClassName, String isFirst, Sort sort);

    List<Medium> findByTicketAndObjClassNameAndIsFirst(String ticket, String objClassName, String isFirst, Sort sort);
}
