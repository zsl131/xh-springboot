package com.zslin.business.mini.tools;

import com.zslin.business.dao.ICustomerDao;
import com.zslin.business.dao.IProductFavoriteRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 客户管理工具
 */
@Component("customerTools")
public class CustomerTools {

    @Autowired
    private IProductFavoriteRecordDao productFavoriteRecordDao;

    @Autowired
    private ICustomerDao customerDao;

    /** 修改用户的昵称和头像 */
    public void updateUserInfo(String nickname, String headimgurl, Integer customId) {
        productFavoriteRecordDao.updateNickname(nickname, customId); //
        customerDao.updateByHql("UPDATE ProductComment p SET p.nickname=?1 WHERE p.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE SearchRecord s SET s.nickname=?1 WHERE s.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE Agent a SET a.nickname=?1 WHERE a.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE ShoppingBasket s SET s.nickname=?1 WHERE s.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE CustomCoupon c SET c.nickname=?1 WHERE c.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE CustomAddress c SET c.nickname=?1 WHERE c.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE Orders o SET o.nickname=?1, o.headImgUrl=?2 WHERE o.customId=?3", nickname, headimgurl, customId);
        customerDao.updateByHql("UPDATE OrdersComment o SET o.customNickname=?1, o.headImgUrl=?2 WHERE o.customId=?3", nickname, headimgurl, customId);
        customerDao.updateByHql("UPDATE UnifiedOrder u SET u.nickname=?1 WHERE u.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE OrdersProduct o SET o.nickname=?1 WHERE o.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE CustomCommissionRecord c SET c.customNickname=?1 WHERE c.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE OrdersAfterSale o SET o.nickname=?1 WHERE o.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE Customer c SET c.nickname=?1, c.headImgUrl=?2 WHERE c.id=?3", nickname, headimgurl, customId);
        customerDao.updateByHql("UPDATE Customer c SET c.leaderNickname=?1 WHERE c.leaderId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE Customer c SET c.inviterNickname=?1 WHERE c.inviterId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE Phonograph p SET p.nickname=?1 WHERE p.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE ImageWall w SET w.customNickname=?1, w.headImgUrl=?2 WHERE w.customId=?3", nickname, headimgurl, customId);
        customerDao.updateByHql("UPDATE ImageComment c SET c.customNickname=?1 WHERE c.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE CustomImageRelation c SET c.customNickname=?1 WHERE c.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE CustomSubscribe c SET c.customNickname=?1 WHERE c.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE OrdersExpress o SET o.customNickname=?1 WHERE o.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE Reward r SET r.customNickname=?1 WHERE r.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE ReceiptRecord r SET r.customNickname=?1 WHERE r.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE SaleRanking s SET s.customNickname=?1 WHERE s.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE CustomMessage c SET c.nickname=?1 WHERE c.customId=?2", nickname, customId);
        customerDao.updateByHql("UPDATE MatterApply m SET m.nickname=?1, m.headImgUrl=?2 WHERE m.customId=?3", nickname,headimgurl,customId);

    }
}
