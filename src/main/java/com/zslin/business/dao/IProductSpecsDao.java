package com.zslin.business.dao;

import com.zslin.business.model.ProductSpecs;
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
public interface IProductSpecsDao extends BaseRepository<ProductSpecs, Integer>, JpaSpecificationExecutor<ProductSpecs> {

    /** 获取分类对应的规格，用于删除分类前的判断 */
    @Query("SELECT COUNT(s.id) FROM ProductSpecs s WHERE s.cateId=?1 ")
    Long findCountByCateId(Integer cateId);

    /** 获取产品对应的规格，用于删除产品前的判断 */
    @Query("SELECT COUNT(s.id) FROM ProductSpecs s WHERE s.proId=?1 ")
    Long findCountByProId(Integer proId);

    List<ProductSpecs> findByProId(Integer proId, Sort sort);

    @Query("FROM ProductSpecs p WHERE p.proId=?1 AND p.amount>0 ")
    List<ProductSpecs> findByProIdAndAmount(Integer proId, Sort sort);

    @Query("SELECT MIN(s.price) FROM ProductSpecs s WHERE s.proId=?1")
    Float queryPrice(Integer proId);

    /** 获取产品的库存 */
    @Query("SELECT SUM(s.amount) FROM ProductSpecs s WHERE s.proId=?1")
    Integer queryProductTotalAmount(Integer proId);

    @Query("SELECT s.amount FROM ProductSpecs s WHERE s.id=?1")
    Integer querySpecsAmount(Integer specsId);

    @Query("FROM ProductSpecs p WHERE p.id in (?1)")
    List<ProductSpecs> findByIds(Integer [] ids);

    @Query("UPDATE ProductSpecs p SET p.amount=p.amount-?1 WHERE p.id=?2")
    @Modifying
    @Transactional
    void minusAmount(Integer amount, Integer id);
}
