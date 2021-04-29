package com.zslin.business.dao;

import com.zslin.business.model.Agent;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IAgentDao extends BaseRepository<Agent, Integer>, JpaSpecificationExecutor<Agent> {

    @Query("UPDATE Agent a SET a.paperCount=?1 WHERE a.id=?2")
    @Modifying
    @Transactional
    void updatePaperCount(Integer count, Integer id);

    @Query("UPDATE Agent a SET a.ordersCount=a.ordersCount+?1 WHERE a.id=?2")
    @Modifying
    @Transactional
    void plusOrdersCount(Integer count, Integer id);

    @Query("UPDATE Agent a SET a.subCount=a.subCount+?1 WHERE a.id=?2")
    @Modifying
    @Transactional
    void plusSubCount(Integer count, Integer id);

    @Query("UPDATE Agent a SET a.relationCount=a.relationCount+?1 WHERE a.id=?2")
    @Modifying
    @Transactional
    void plusRelationCount(Integer count, Integer id);

    @Query("UPDATE Agent a SET a.verifyCount=a.verifyCount+?1 WHERE a.id=?2")
    @Modifying
    @Transactional
    void plusVerifyCount(Integer count, Integer id);

//    Agent findByUnionid(String unionid);

    Agent findByOpenid(String openid);

    Agent findByCustomId(Integer customId);

    /** 通过顾客ID获取代理ID */
    @Query("SELECT a.id FROM Agent a WHERE a.customId=?1")
    Integer queryAgentId(Integer customId);

    @Query("UPDATE Agent a SET a.phone=?1 WHERE a.customId=?2")
    @Modifying
    @Transactional
    void updatePhone(String phone, Integer customId);

    /** 通过Code获取对象 */
    Agent findByOwnCode(String code);

    /** 获取正常代理 */
    @Query("FROM Agent a WHERE a.openid=?1 AND a.status='1' ")
    Agent findOkByOpenid(String openid);
}
