package com.zslin.business.mini.service;

import com.zslin.business.dao.IOrdersAfterSaleDao;
import com.zslin.business.dao.IOrdersDao;
import com.zslin.business.dao.IOrdersProductDao;
import com.zslin.business.model.OrdersAfterSale;
import com.zslin.business.model.OrdersProduct;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.qiniu.tools.QiniuTools;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@HasTemplateMessage
public class MiniOrdersProductService {

    @Autowired
    private IOrdersProductDao ordersProductDao;

    @Autowired
    private IOrdersDao ordersDao;

    @Autowired
    private QiniuTools qiniuTools;

    @Autowired
    private IOrdersAfterSaleDao ordersAfterSaleDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @NeedAuth(openid = true)
    public JsonResult loadPro(String params) {
        Integer id = JsonTools.getId(params);
        OrdersProduct op = ordersProductDao.findOne(id);

        OrdersAfterSale oaSale = ordersAfterSaleDao.findByOrderProId(id);
        return JsonResult.success().set("product", op).set("oaSale", oaSale);
    }

    /** 删除售后凭证 */
    public JsonResult removeImg(String params) {
        try {
            String url = JsonTools.getJsonParam(params, "url");
            String key = url.substring(url.lastIndexOf("/")+1);
            qiniuTools.deleteFile(key);
            return JsonResult.success("操作成功");
        } catch (Exception e) {
            return JsonResult.error("操作失败");
        }
    }

    @NeedAuth(openid = true)
    @TemplateMessageAnnotation(name = "售后申请通知", keys = "顾客信息-联系方式-订单号-商品金额-其他信息")
    public JsonResult add(String params) {
        try {
            Integer id = JsonTools.getId(params); //OrdersProduct的ID
            OrdersAfterSale oas = ordersAfterSaleDao.findByOrderProId(id);
            if(oas!=null) {
                return JsonResult.success("已经申请售后，不能重复申请").set("flag", "0");
            }
            String content = JsonTools.getJsonParam(params, "msg");
            String imgs = JsonTools.getJsonParam(params, "imgs");
            String phone = JsonTools.getJsonParam(params, "phone");

            OrdersProduct op = ordersProductDao.findOne(id);
            oas = new OrdersAfterSale();
            oas.setContent(content);
            oas.setPhone(phone);
            oas.setImgs(imgs);
            oas.setOpenid(op.getOpenid());
            oas.setOrderProId(id);
            oas.setOrdersId(op.getOrdersId());
            oas.setOrdersNo(op.getOrdersNo());
            oas.setProId(op.getProId());
            oas.setProTitle(op.getProTitle());
            oas.setSpecsId(op.getSpecsId());
            oas.setSpecsName(op.getSpecsName());
            oas.setHasRefund("0"); //默认为0，没有产生退款
            oas.setCreateDay(NormalTools.curDate());
            oas.setCreateTime(NormalTools.curDatetime());
            oas.setCreateLong(System.currentTimeMillis());
            oas.setNickname(op.getNickname());
            oas.setCustomId(op.getCustomId());
            oas.setOpenid(op.getOpenid());
            oas.setOriPrice(op.getOriPrice());
            oas.setPrice(op.getPrice());

            ordersAfterSaleDao.save(oas);

            //"顾客信息-联系方式-订单号-商品金额-其他信息"
            sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "售后申请通知", "", "有订单提交了售后申请",
                    TemplateMessageTools.field("顾客信息", oas.getNickname()),
                    TemplateMessageTools.field("联系方式", phone),
                    TemplateMessageTools.field("订单号", oas.getOrdersNo()),
                    TemplateMessageTools.field("商品金额", op.getPrice()+""),
                    TemplateMessageTools.field("其他信息", "请及时处理"),

                    TemplateMessageTools.field(content));

            return JsonResult.success("已经提交申请，请等待客服答复").set("flag", "1");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.success("操作失败").set("flag", "0");
        }
    }
}
