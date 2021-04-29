package com.zslin.business.dao;

import com.zslin.business.model.CustomCoupon;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface ICustomCouponDao extends BaseRepository<CustomCoupon, Integer>, JpaSpecificationExecutor<CustomCoupon> {

    List<CustomCoupon> findByRuleSnAndReceiveKeyAndCustomId(String ruleSn, String receiveKey, Integer customId);

    /**
     * 获取用户优惠券
     * @param customId
     * @param hasRead
     * @return
     */
    List<CustomCoupon> findByCustomIdAndHasRead(Integer customId, String hasRead);

    @Query("FROM CustomCoupon c WHERE c.customId=?1 AND c.status='1' AND c.reachMoney<=?2 AND (c.proId IN ?3 OR c.proId IS NULL OR c.proId=0)")
    List<CustomCoupon> findByCanUse(Integer customId, Float reachMoney, Integer [] ids);

    @Query("UPDATE CustomCoupon c SET c.status=?1 WHERE c.id=?2")
    @Modifying
    @Transactional
    void updateStatus(String status, Integer id);
}
