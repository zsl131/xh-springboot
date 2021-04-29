package com.zslin.business.tools;

import com.zslin.business.dao.IProductTagDao;
import com.zslin.business.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 产品工具类，主要用于更新产品同时更新其他相关数据
 */
@Component("productTools")
public class ProductTools {

    @Autowired
    private IProductTagDao productTagDao;

    /**
     * 修改Product时的关联更新
     * @param pro
     */
    @Transactional
    public void onUpdateProduct(Product pro) {
        //更新ProductTag
        productTagDao.updateByHql("UPDATE ProductTag p SET p.proTitle=?1 WHERE p.proId=?2", pro.getTitle(), pro.getId());
        //更新ProductSpecs
        productTagDao.updateByHql("UPDATE ProductSpecs p SET p.proTitle=?1 WHERE p.proId=?2", pro.getTitle(), pro.getId());
        //更新ProductComment
        productTagDao.updateByHql("UPDATE ProductComment p SET p.proTitle=?1 WHERE p.proId=?2", pro.getTitle(), pro.getId());
        //更新ProductSpecsRate
        productTagDao.updateByHql("UPDATE AgentLevelSpecsRate p SET p.proTitle=?1 WHERE p.proId=?2", pro.getTitle(), pro.getId());
        //更新ShoppingBasket
        productTagDao.updateByHql("UPDATE ShoppingBasket p SET p.proTitle=?2,p.proImg=?3 WHERE p.proId=?1", pro.getId(), pro.getTitle(), pro.getHeadImgUrl());
        //更新Coupon
        productTagDao.updateByHql("UPDATE Coupon p SET p.proTitle=?2 WHERE p.proId=?1", pro.getId(), pro.getTitle());
        //更新CustomCoupon
        productTagDao.updateByHql("UPDATE CustomCoupon p SET p.proTitle=?2 WHERE p.proId=?1", pro.getId(), pro.getTitle());
        //更新OrdersProduct
        productTagDao.updateByHql("UPDATE OrdersProduct p SET p.proTitle=?2 WHERE p.proId=?1", pro.getId(), pro.getTitle());
        //更新ProductFavoriteRecord
        productTagDao.updateByHql("UPDATE ProductFavoriteRecord p SET p.proTitle=?2,p.proImg=?3 WHERE p.proId=?1", pro.getId(), pro.getTitle(), pro.getHeadImgUrl());
    }
}
