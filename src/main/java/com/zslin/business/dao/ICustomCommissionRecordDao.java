package com.zslin.business.dao;

import com.zslin.business.mini.dto.AgentCommissionDto;
import com.zslin.business.model.CustomCommissionRecord;
import com.zslin.business.settlement.dto.RankingDto;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2020-02-20.
 */
public interface ICustomCommissionRecordDao extends BaseRepository<CustomCommissionRecord, Integer>, JpaSpecificationExecutor<CustomCommissionRecord> {

    List<CustomCommissionRecord> findByOrdersId(Integer ordersId);

    //TODO 状态为提现成功后不可再修改
    @Query("UPDATE CustomCommissionRecord c SET c.status=?1 WHERE c.ordersNo=?2 AND c.status!='4'")
    @Modifying
    @Transactional
    void updateStatus(String status, String ordersNo);

    /** 设置非自动抵扣佣金的状态 */
    @Query("UPDATE CustomCommissionRecord c SET c.status=?1 WHERE c.ordersNo=?2 AND c.isAuto!='1'")
    @Modifying
    @Transactional
    void updateStatusByNormal(String status, String ordersNo);

    /** 设置自动抵扣佣金的状态 */
    @Query("UPDATE CustomCommissionRecord c SET c.status=?1, c.cashOutBatchNo=?2 WHERE c.ordersNo=?3 AND c.isAuto='1'")
    @Modifying
    @Transactional
    void updateStatusByAuto(String status, String batchNo, String ordersNo);

    @Query("UPDATE CustomCommissionRecord c SET c.status=?1 WHERE c.ordersNo=?2 AND c.cashOutBatchNo IS NULL ")
    @Modifying
    @Transactional
    void updateStatusNoBatchNo(String status, String ordersNo);

    List<CustomCommissionRecord> findByOrdersNoAndProId(String ordersNo, Integer proId);

    //Integer agentId, String haveType, String status, Float money, Integer totalCount
//    @Query("SELECT new com.zslin.business.mini.dto.AgentCommissionDto(c.agentId, c.haveType, c.status, SUM(c.money), SUM(c.specsCount)) FROM CustomCommissionRecord c WHERE c.status=?1 AND c.agentId=?2 AND c.cashOutBatchNo IS NULL ")
    @Query("SELECT new com.zslin.business.mini.dto.AgentCommissionDto(c.agentId, c.haveType, c.status, SUM(c.money), SUM(c.specsCount)) FROM CustomCommissionRecord c WHERE c.status=?1 AND c.agentId=?2 AND c.saleFlag!='2' ")
    AgentCommissionDto queryCountDto(String status, Integer agentId);

    @Query("SELECT new com.zslin.business.mini.dto.AgentCommissionDto(c.agentId, c.haveType, c.status, SUM(c.money), SUM(c.specsCount)) FROM CustomCommissionRecord c WHERE c.status=?1 AND c.agentId=?2 AND c.saleFlag!='2'  AND c.cashOutBatchNo IS NULL AND c.isAuto!='1' ")
    AgentCommissionDto queryCountDtoNoBatchNo(String status, Integer agentId);

    /** 设置本该修改为提现的数据 */
    /*@Query("UPDATE CustomCommissionRecord c, CashOut o SET " +
            " c.status=?1, c.cashOutDay=o.createDay, c.cashOutTime=o.createTime, c.cashOutLong=o.createLong WHERE " +
            " o.batchNo=c.cashOutBatchNo AND c.status='2' AND o.status='0'")
    void update2CashOut(String status);*/

    /** 设置本该修改为纳入结算的数据 */
    /*@Query("UPDATE CustomCommissionRecord c, CashOut o SET " +
            " c.status=?1, c.payOutDay=o.payDate, c.payOutTime=o.payTime, c.payOutLong=o.payLong WHERE " +
            " o.batchNo=c.cashOutBatchNo AND c.status='3' AND o.status='1'")
    void update2PayOut(String status);*/

    @Query("UPDATE CustomCommissionRecord c SET c.cashOutBatchNo=?1, c.status=?2," +
            " c.cashOutDay=?3, c.cashOutTime=?4, c.cashOutLong=?5 WHERE " +
            " c.status=?6 AND c.agentId=?7 AND c.cashOutBatchNo IS NULL ")
    @Modifying
    @Transactional
    void updateBatchNo(String batchNo, String newStatus, String outDay, String outTime, Long outLong
            , String oldStatus, Integer agentId);

    @Query("UPDATE CustomCommissionRecord c SET c.status=?1, c.payOutDay=?2, c.payOutTime=?3,c.payOutLong=?4" +
            " WHERE c.cashOutBatchNo=?5 AND c.agentId=?6 ")
    @Modifying
    @Transactional
    void updateStatusByBatchNo(String status, String payOutDay, String payOutTime, Long payOutLong,
                               String batchNo, Integer agentId);

    //获取排名信息
    //Integer agentId, String agentName, String agentPhone, Integer customId, String customNickname, Long specsCount, Double commissionMoney
    //haveType=0表示必须是自己推广的
    @Query("SELECT new com.zslin.business.settlement.dto.RankingDto" +
            "(c.agentId, a.name, a.phone, a.customId, a.nickname, a.openid, COUNT(c.id) as totalCount, SUM(c.money) as totalMoney) " +
            "FROM CustomCommissionRecord c, Agent a WHERE c.agentId=a.id AND c.haveType='0' AND c.createMonth=?1 AND c.status in ('1', '2', '3', '4', '5') " +
            " GROUP BY c.agentId ")
    Page<RankingDto> queryRanking(String createMonth, Pageable pageable);

    /** 获取用户指定月份是否有业绩，只要用户付款即表示有业绩，需要是自己推广的 */
    @Query("SELECT COUNT(c.id) FROM CustomCommissionRecord c WHERE c.createMonth=?1 AND c.haveType='0' AND c.agentOpenid=?2 AND " +
            "c.status in ('1', '2', '3', '4', '5')")
    Long queryCount(String createMonth, String agentOpenid);
}
