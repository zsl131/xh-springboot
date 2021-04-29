package com.zslin.business.app.tools;

import com.zslin.business.dao.ICouponDao;
import com.zslin.business.dao.ICustomCouponDao;
import com.zslin.business.model.Coupon;
import com.zslin.business.model.CustomCoupon;
import com.zslin.business.model.Customer;
import com.zslin.core.common.NormalTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 优惠券工具类
 */
@Component("couponTools")
public class CouponTools {

    @Autowired
    private ICustomCouponDao customCouponDao;

    @Autowired
    private ICouponDao couponDao;

    public static final String FIRST_FOLLOW = "FIRST_FOLLOW";

    /**
     * 处理初次关注时赠送优惠券
     */
    public void handlerFirstFollowCoupon(Customer customer) {
        //先暂时取消自动获取抵价券功能
        /*List<CustomCoupon> couponList = customCouponDao.findByRuleSnAndReceiveKeyAndCustomId(FIRST_FOLLOW, customer.getId()+"", customer.getId());
        if(couponList==null || couponList.size()<=0) { //如果还没有赠送
//            Coupon c = couponDao.findByRuleSn(FIRST_FOLLOW);
            List<Coupon> couList = couponDao.findCoupons(FIRST_FOLLOW); //获取可以获取的抵价券
            for(Coupon c : couList) {
                CustomCoupon coupon = new CustomCoupon();
                coupon.setCanRepeat(c.getCanRepeat());
                coupon.setCouponId(c.getId());
                coupon.setCouponName(c.getName());
                coupon.setCreateDay(NormalTools.curDate());
                coupon.setCreateLong(System.currentTimeMillis());
                coupon.setCreateTime(NormalTools.curDatetime());
                coupon.setCustomId(customer.getId());
                coupon.setOpenid(customer.getOpenid());
                coupon.setNickname(customer.getNickname());
                coupon.setProId(c.getProId());
                coupon.setProTitle(c.getProTitle());
                coupon.setReceiveKey(customer.getId() + "");
                coupon.setRemark(c.getRemark());
                coupon.setRuleSn(FIRST_FOLLOW);
                coupon.setStatus("1");
                coupon.setUnionid(customer.getUnionid());
                coupon.setWorth(c.getWorth());
                coupon.setHasRead("0");
                coupon.setEndLong(buildEndLong(c.getDuration()));
                coupon.setEndTime(buildEndTime(c.getDuration()));
                coupon.setReachMoney(c.getReachMoney());
                customCouponDao.save(coupon);
            }
        }*/
    }

    private boolean checkExists(List<CustomCoupon> couponList, Integer couponId) {
        if(couponList==null || couponList.size()<=0) {return false;}
        boolean res = false;
        for(CustomCoupon cc : couponList) {
            if(cc.getCouponId().equals(couponId)) {
                res = true; break;
            }
        }
        return res;
    }

    /**
     * 处理初次关注时赠送优惠券
     */
    public void handlerFirstCoupon(Customer customer) {
        //先暂时取消自动获取抵价券功能
        List<CustomCoupon> couponList = customCouponDao.findByRuleSnAndReceiveKeyAndCustomId(FIRST_FOLLOW, customer.getId()+"", customer.getId());
        List<Coupon> couList = couponDao.findCoupons(FIRST_FOLLOW); //获取可以获取的抵价券
        if(couponList==null || couponList.size()<couList.size()) { //如果还没有赠送
//            Coupon c = couponDao.findByRuleSn(FIRST_FOLLOW);
            for(Coupon c : couList) {
                if(!checkExists(couponList, c.getId())) { //如果不存在，则添加
                    CustomCoupon coupon = new CustomCoupon();
                    coupon.setCanRepeat(c.getCanRepeat());
                    coupon.setCouponId(c.getId());
                    coupon.setCouponName(c.getName());
                    coupon.setCreateDay(NormalTools.curDate());
                    coupon.setCreateLong(System.currentTimeMillis());
                    coupon.setCreateTime(NormalTools.curDatetime());
                    coupon.setCustomId(customer.getId());
                    coupon.setOpenid(customer.getOpenid());
                    coupon.setNickname(customer.getNickname());
                    coupon.setProId(c.getProId());
                    coupon.setProTitle(c.getProTitle());
                    coupon.setReceiveKey(customer.getId() + "");
                    coupon.setRemark(c.getRemark());
                    coupon.setRuleSn(FIRST_FOLLOW);
                    coupon.setStatus("1");
                    coupon.setUnionid(customer.getUnionid());
                    coupon.setWorth(c.getWorth());
                    coupon.setHasRead("0");
                    coupon.setEndLong(buildEndLong(c.getDuration()));
                    coupon.setEndTime(buildEndTime(c.getDuration()));
                    coupon.setReachMoney(c.getReachMoney());
                    customCouponDao.save(coupon);

                    couponDao.plusAmount(1, c.getId());
                }
            }
        }
    }

    private Long buildEndLong(Integer duration) {
        Long curLong = System.currentTimeMillis();
        Long endLong = curLong + (duration*1000);
        return endLong;
    }

    private String buildEndTime(Integer duration) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.now();
        LocalDateTime res = ldt.plusSeconds(duration);
        return df.format(res);

    }
}
