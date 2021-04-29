package com.zslin.business.dao;

import com.zslin.business.model.Product;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
@Component("productDao")
public interface IProductDao extends BaseRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    /** 获取分类下的产品数量，用于删除分类前的判断 */
    @Query("SELECT COUNT(p.id) FROM Product p WHERE p.cateId=?1 ")
    Long findCountByCateId(Integer cateId);

    List<Product> findByCateId(Integer cateId);

    @Query("UPDATE Product p SET p.status=?1 WHERE p.id=?2 ")
    @Modifying
    @Transactional
    void updateStatus(String status, Integer id);

    @Query("UPDATE Product p SET p.isRecommend=?1 WHERE p.id=?2 ")
    @Modifying
    @Transactional
    void updateRecommend(String flag, Integer id);

    @Query("UPDATE Product p SET p.specsCount=p.specsCount+?1 WHERE p.id=?2 ")
    @Modifying
    @Transactional
    void plusSpecsCount(Integer amount, Integer id);

    @Query("UPDATE Product p SET p.readCount=p.readCount+?1 WHERE p.id=?2 ")
    @Modifying
    @Transactional
    void plusReadCount(Integer amount, Integer id);

    @Query("UPDATE Product p SET p.favoriteCount=p.favoriteCount+?1 WHERE p.id=?2 ")
    @Modifying
    @Transactional
    void plusFavoriteCount(Integer amount, Integer id);

    @Query("UPDATE Product p SET p.saleMode=?1, p.deliveryDate=?2 WHERE p.id=?3")
    @Modifying
    @Transactional
    void updateMode(String mode, String deliveryDate, Integer id);

    @Query("UPDATE Product p SET p.headImgUrl=?1 WHERE p.id=?2")
    @Modifying
    @Transactional
    void updateHeadimgUrl(String headimg, Integer id);

    /**
     * 设置显示的价格
     * @param price
     * @param id
     */
    @Query("UPDATE Product p SET p.price=?1 WHERE p.id=?2")
    @Modifying
    @Transactional
    void updatePrice(Float price, Integer id);

    @Query("SELECT p FROM Product p WHERE p.title LIKE %?1%")
    List<Product> searchByTitle(String title);

    @Query("FROM Product p WHERE p.id in ?1")
    List<Product> findByIds(Integer [] ids);

    /** 修改库存 */
    @Query("UPDATE Product p SET p.surplusCount=?1 WHERE p.id=?2")
    @Modifying
    @Transactional
    void updateSurplus(Integer amount, Integer id);

    /** 增加销量 */
    @Query("UPDATE Product p SET p.saleCount=p.saleCount+?1 WHERE p.id=?2")
    @Modifying
    @Transactional
    void plusSaleCount(Integer amount, Integer id);
}
