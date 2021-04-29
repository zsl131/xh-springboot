package com.zslin.business.finance.dao;

import com.zslin.business.finance.model.FinanceTicket;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by zsl on 2019/1/9.
 */
public interface IFinanceTicketDao extends BaseRepository<FinanceTicket, Integer>, JpaSpecificationExecutor<FinanceTicket> {

    List<FinanceTicket> findByTicketNo(String ticketNo);
}
