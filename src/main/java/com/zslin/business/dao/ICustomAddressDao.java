package com.zslin.business.dao;

import com.zslin.business.model.CustomAddress;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface ICustomAddressDao extends BaseRepository<CustomAddress, Integer>, JpaSpecificationExecutor<CustomAddress> {

    List<CustomAddress> findByCustomId(Integer customId, Sort sort);

    @Query("UPDATE CustomAddress c SET c.isDefault='0' WHERE c.isDefault='1'")
    @Modifying
    @Transactional
    void cleanDefault();

    @Query("UPDATE CustomAddress c SET c.isDefault=?1 WHERE c.id=?2")
    @Modifying
    @Transactional
    void updateDefault(String isDefault, Integer id);

    CustomAddress findByCustomIdAndId(Integer customId, Integer id);

    @Modifying
    @Transactional
    void deleteByCustomIdAndId(Integer customId, Integer id);

    /** 获取默认地址 */
    @Query("FROM CustomAddress c WHERE c.isDefault='1' AND c.customId=?1")
    CustomAddress findDefaultAddress(Integer customId);
}
