package com.zslin.business.finance.dao;

import com.zslin.business.finance.model.FinanceRecord;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zsl on 2019/1/9.
 */
public interface IFinanceRecordDao extends BaseRepository<FinanceRecord, Integer>, JpaSpecificationExecutor<FinanceRecord> {

    @Query("SELECT SUM(amount) FROM FinanceRecord WHERE status='1' AND flag=?1")
    Float sum(String flag);

    @Query("SELECT MAX(tno) FROM FinanceRecord WHERE recordMonth=?1")
    Integer maxTicketNo(String month);

    FinanceRecord findByTicketNo(String ticketNo);

    @Query("UPDATE FinanceRecord SET status=?1, invalidReason=?2, invalidName=?3, verifyName=?3, invalidPhone=?4, verifyTime=?5 WHERE id=?6")
    @Modifying
    @Transactional
    void updateStatusByInvalid(String status, String reason, String name, String phone, String verifyTime, Integer id);

    @Query("UPDATE FinanceRecord SET status=?1, verifyName=?2, verifyTime=?3 WHERE id=?4")
    @Modifying
    @Transactional
    void updateStatusByPass(String status, String name, String verifyTime, Integer id);
}
