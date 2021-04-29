package com.zslin.business.dao;

import com.zslin.business.model.CouponRuleDetail;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2020-08-08.
 */
public interface ICouponRuleDetailDao extends BaseRepository<CouponRuleDetail, Integer>, JpaSpecificationExecutor<CouponRuleDetail> {

    @Query("SELECT r.couponId FROM CouponRuleDetail r WHERE r.ruleId=?1")
    List<Integer> queryIds(Integer ruleId);

    CouponRuleDetail findByRuleIdAndCouponId(Integer ruleId, Integer couponId);

    @Transactional
    @Modifying
    @Query("DELETE CouponRuleDetail cr WHERE cr.ruleId=?1")
    void deleteByRuleId(Integer ruleId);
}
