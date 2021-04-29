package com.zslin.business.settlement.dao;

import com.zslin.business.settlement.model.SaleRanking;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2020-04-25.
 */
public interface ISaleRankingDao extends BaseRepository<SaleRanking, Integer>, JpaSpecificationExecutor<SaleRanking> {

    /** 获取是否已经添加 */
    @Query("SELECT COUNT(s.id) FROM SaleRanking s WHERE s.belongMonth=?1")
    Long queryCount(String month);

    /** 获取最大排序号 */
    @Query("SELECT MAX(s.orderNo) FROM SaleRanking s WHERE s.belongMonth=?1 ")
    Integer queryMaxOrderNo(String month);

    /**
     * 获取满足条件的排名
     * @param month 月份
     * @param baseMoney 起始金额
     * @return
     */
    @Query("FROM SaleRanking s WHERE s.belongMonth=?1 AND s.commissionMoney>=?2")
    Page<SaleRanking> findByMonth(String month, Float baseMoney, Pageable pageable);

    /** 查寻个人的排名信息 */
    @Query("FROM SaleRanking s WHERE s.belongMonth=?1 AND s.customOpenid=?2")
    SaleRanking findOne(String month, String agentOpenid);
}
