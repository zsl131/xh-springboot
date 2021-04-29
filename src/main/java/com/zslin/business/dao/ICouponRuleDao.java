package com.zslin.business.dao;

import com.zslin.business.model.CouponRule;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 钟述林 on 2020-02-09.
 */
public interface ICouponRuleDao extends BaseRepository<CouponRule, Integer>, JpaSpecificationExecutor<CouponRule> {

    CouponRule findByRuleSn(String ruleSn);
}
