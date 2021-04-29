package com.zslin.business.tools;

import com.zslin.business.app.tools.CouponTools;
import com.zslin.business.dao.ICustomerDao;
import com.zslin.business.model.Customer;
import com.zslin.business.wx.dao.IWxAccountDao;
import com.zslin.business.wx.dao.IWxMiniDao;
import com.zslin.business.wx.model.WxAccount;
import com.zslin.business.wx.model.WxMini;
import com.zslin.core.cache.CacheTools;
import com.zslin.core.common.NormalTools;
import com.zslin.core.tools.RandomTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 小程序微信绑定工具类
 */
@Component
public class BindCodeTools {

    @Autowired
    private CacheTools cacheTools;

    @Autowired
    private IWxMiniDao wxMiniDao;

    @Autowired
    private IWxAccountDao wxAccountDao;

    @Autowired
    private ICustomerDao customerDao;

    @Autowired
    private CouponTools couponTools;

    /**
     * 生成随机数存入cache
     * @param miniOpenid 小程序获取到的openid
     * @return 返回获取到的随机数
     */
    public String getCode(String miniOpenid) {
        String randomCode = RandomTools.genCode4(); //生成6位数
        while(cacheTools.exists(randomCode)) { //如果code存在，都需要重新获取
            randomCode = RandomTools.genCode4(); //
        }
        cacheTools.putKey(randomCode, miniOpenid, 60*10);
        return randomCode;
    }

    public String bindWxMini(String code, String wxOpenid) {
        try {
            String miniOpenid = (String) cacheTools.getKey(code);
            if(miniOpenid!=null && !"".equals(miniOpenid)) { //如果存在
                WxMini wm = wxMiniDao.findByMiniOpenid(miniOpenid);
                Customer customer = customerDao.findByOpenid(miniOpenid);
                if(wm==null && wxMiniDao.findByWxOpenid(wxOpenid)==null) { //需要绑定
                    wm = new WxMini();
                    WxAccount account = wxAccountDao.findByOpenid(wxOpenid);

                    wm.setAccountId(account.getId());
                    wm.setCreateDay(NormalTools.curDate());
                    wm.setCreateLong(System.currentTimeMillis());
                    wm.setCreateTime(NormalTools.curDatetime());
                    wm.setCustomId(customer.getId());
                    wm.setMiniOpenid(miniOpenid);
                    wm.setNickname(customer.getNickname());
                    wm.setWxOpenid(wxOpenid);
                    wxMiniDao.save(wm);

                    customerDao.updateBindWx("1", miniOpenid); //修改绑定标记
//                    couponTools.handlerFirstCoupon(customer);
//                    return "微信与小程序绑定成功";
//                    return buildStr();
                } else {
//                    couponTools.handlerFirstCoupon(customer); //绑定没绑定都先检测是否有券可领
//                    return "已经绑定，不用重复操作";
                }
                couponTools.handlerFirstCoupon(customer); //绑定没绑定都先检测是否有券可领
                return buildStr();

            } else {
                return "验证码【"+code+"】不存在或已过期";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "绑定出错！";
        }
    }

    private String buildStr() {
        return "成功领取抵价券。请在“满山晴”小程序中“我的”->“我的抵价券”查看使用";
    }
}
