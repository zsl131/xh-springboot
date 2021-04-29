package com.zslin.business.dao;

import com.zslin.business.model.ShoppingBasket;
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
public interface IShoppingBasketDao extends BaseRepository<ShoppingBasket, Integer>, JpaSpecificationExecutor<ShoppingBasket> {

    ShoppingBasket findByProIdAndSpecsIdAndOpenid(Integer proId, Integer specsId, String openid);

    List<ShoppingBasket> findByOpenid(String openid, Sort sort);

    @Query("SELECT SUM(s.amount) FROM ShoppingBasket s WHERE s.openid=?1")
    Integer queryCount(String openid);

    @Query("UPDATE ShoppingBasket s SET s.amount=s.amount+?1 WHERE s.id=?2")
    @Modifying
    @Transactional
    void plusAmount(Integer amount, Integer id);

    @Query("UPDATE ShoppingBasket s SET s.amount=?1 WHERE s.id=?2")
    @Modifying
    @Transactional
    void updateAmount(Integer amount, Integer id);

    @Query("DELETE ShoppingBasket s WHERE s.id in ?1")
    @Modifying
    @Transactional
    void deleteBasket(Integer [] ids);

    @Query("FROM ShoppingBasket s WHERE s.id in ?1")
    List<ShoppingBasket> findByIds(Integer [] ids);

    /** 获取对应规格库存 */
    @Query("SELECT s.amount FROM ProductSpecs s, ShoppingBasket b WHERE s.proId=b.proId AND s.id=b.specsId AND b.id=?1")
    Integer querySpecsAmount(Integer basketId);
}
