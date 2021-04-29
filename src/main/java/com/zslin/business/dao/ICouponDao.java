package com.zslin.business.dao;

import com.zslin.business.model.Coupon;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface ICouponDao extends BaseRepository<Coupon, Integer>, JpaSpecificationExecutor<Coupon> {

    @Query("SELECT c FROM Coupon c WHERE c.name LIKE %?1%")
    List<Coupon> searchByName(String name);

    @Query("SELECT c FROM Coupon c, CouponRule r WHERE c.id=r.couponId AND r.ruleSn=?1")
    Coupon findByRuleSn(String ruleSn);

    List<Coupon> findByProId(Integer proId);

    @Query("FROM Coupon c WHERE c.id in (SELECT d.couponId FROM CouponRuleDetail d, CouponRule r WHERE r.id=d.ruleId AND r.ruleSn=?1) ")
    List<Coupon> findCoupons(String ruleSn);

    @Query("UPDATE Coupon c SET c.surplusCount=c.surplusCount-?1, c.receiveCount=c.receiveCount+?1 WHERE c.id=?2")
    @Modifying
    @Transactional
    void plusAmount(Integer amount, Integer id);
}
