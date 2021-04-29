package com.zslin.business.dao;

import com.zslin.business.model.Customer;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2019-12-01.
 */
public interface ICustomerDao extends BaseRepository<Customer, Integer>, JpaSpecificationExecutor<Customer> {

    Customer findByOpenid(String openid);

    @Query("UPDATE Customer c SET c.name=?1, c.phone=?2, c.agentId=?3 WHERE c.openid=?4")
    @Modifying
    @Transactional
    void updateName(String name, String phone, Integer agentId, String openid);

    Customer findByPhone(String phone);

    @Query("UPDATE Customer c SET c.phone=?1 WHERE c.id=?2")
    @Modifying
    @Transactional
    void updatePhone(String phone, Integer id);

    @Query("UPDATE Customer c SET c.bindWx=?1 WHERE c.openid=?2 ")
    @Modifying
    @Transactional
    void updateBindWx(String flag, String openid);
}
