package com.zslin.business.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.app.dto.OrdersCommissionDto;
import com.zslin.business.app.dto.ProductSpecsDto;
import com.zslin.business.app.dto.SubmitOrdersDto;
import com.zslin.business.app.tools.CommissionTools;
import com.zslin.business.app.tools.OrdersHandlerTools;
import com.zslin.business.dao.*;
import com.zslin.business.mini.dto.PaySubmitDto;
import com.zslin.business.mini.dto.RefundDto;
import com.zslin.business.mini.tools.MiniUtils;
import com.zslin.business.mini.tools.PayTools;
import com.zslin.business.model.*;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.LoginUserDto;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.rabbit.RabbitNormalTools;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.repository.SpecificationOperator;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import com.zslin.core.tools.RandomTools;
import com.zslin.core.tools.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@HasTemplateMessage
public class MiniOrdersService {

    @Autowired
    private IShoppingBasketDao shoppingBasketDao;

    @Autowired
    private ICustomAddressDao customAddressDao;

    @Autowired
    private ICustomCouponDao customCouponDao;

    @Autowired
    private IProductSpecsDao productSpecsDao;

    @Autowired
    private IProductDao productDao;

    @Autowired
    private RabbitNormalTools rabbitNormalTools;

    @Autowired
    private IOrdersDao ordersDao;

    @Autowired
    private OrdersHandlerTools ordersHandlerTools;

    @Autowired
    private IOrdersProductDao ordersProductDao;

    @Autowired
    private ICustomCommissionRecordDao customCommissionRecordDao;

    @Autowired
    private PayTools payTools;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @Autowired
    private IRemindOrdersDao remindOrdersDao;

    @Autowired
    private IOrdersExpressDao ordersExpressDao;

    @Autowired
    private IRefundRecordDao refundRecordDao;

    @Autowired
    private CommissionTools commissionTools;

    /** 处理售后 */
    public JsonResult afterSale(String params) {
//System.out.println("-------------MiniOrdersService.afterSale-----------"+params);


        //TODO 还需要处理退款信息
        LoginUserDto userDto = JsonTools.getUser(params);
        //String beanName, String methodName
//        rabbitNormalTools.updateData("payTools", "refund", orders, ordersProduct, userDto, money, reason);
//        payTools.refund(orders, ordersProduct, userDto, money, reason);
        RefundDto dto = payTools.refund(params, userDto);

        return JsonResult.success("退款成功").set("res", dto);
    }

    /**
     * 确认收货
     * @param params
     * @return
     */
    @NeedAuth(openid = true)
    public JsonResult confirmOrders(String params) {
        try {
            WxCustomDto customDto = JsonTools.getCustom(params);
            String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
//        int count = ordersDao.updateStatus("3", ordersNo, customDto.getCustomId());
            Orders orders = ordersDao.findByOrdersNoAndCustomId(ordersNo, customDto.getCustomId());
            orders.setStatus("3");
            orders.setEndLong(System.currentTimeMillis());
            orders.setEndTime(NormalTools.curDatetime());
            orders.setEndDay(NormalTools.curDate());
            ordersDao.save(orders);

            customCommissionRecordDao.updateStatusNoBatchNo("2", ordersNo); //修改提成状态
            return JsonResult.success("确认成功").set("flag", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.success("确认成功").set("flag", "0");
    }

    /** 删除订单 */
    @NeedAuth(openid = true)
    public JsonResult removeOrders(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
        Orders orders = ordersDao.findByOrdersNoAndCustomId(ordersNo, customDto.getCustomId());
        String status = orders.getStatus();
        if("0".equals(status) || "-1".equals(status)) {
            //删除订单
//            ordersDao.updateStatus("-10", ordersNo, customDto.getCustomId());
            orders.setStatus("-10");
            ordersDao.save(orders);
            return JsonResult.success("订单删除成功");
        } else {
            return JsonResult.success("此订单不可删除");
        }
    }

    /** 订单退款 - 客户自行 */
    @NeedAuth(openid = true)
    @TemplateMessageAnnotation(name = "退款申请通知", keys = "订单编号-订单金额")
    public JsonResult refundOrders(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
        String reason = JsonTools.getJsonParam(params, "reason");
        Orders orders = ordersDao.findByOrdersNoAndCustomId(ordersNo, customDto.getCustomId());
        String flag = orders.getRefundFlag();
        if(flag==null || "".equals(flag) || "0".equals(flag) || "-1".equals(flag)) { //如果是 未申请或驳回申请 才能退款
            //删除订单
//            ordersDao.updateStatus("-10", ordersNo, customDto.getCustomId());
//            orders.setStatus("-10");

            String refundNo = orders.getId()+"-"+ RandomTools.genCodeNew();

            //获取产品信息
            List<OrdersProduct> productList = ordersProductDao.findByOrdersNo(ordersNo);
            for(OrdersProduct op : productList) {

                RefundRecord rr = new RefundRecord();
                rr.setType("1");
                rr.setResCodeDes("");
                rr.setResCode("");
                rr.setStatus("0");
                rr.setReason(reason);
                rr.setRefundNo(refundNo);
                rr.setOrdersNo(ordersNo);
                rr.setOrdersId(orders.getId());
                rr.setCreateTime(NormalTools.curDatetime());
                rr.setCreateLong(System.currentTimeMillis());
                rr.setCreateDay(NormalTools.curDate());
//                rr.setBackMoney(orders.getTotalMoney() - (orders.getDiscountMoney() == null ? 0 : orders.getDiscountMoney()));
                rr.setBackMoney(op.getAmount()*op.getPrice() - (orders.getDiscountMoney() == null ? 0 : orders.getDiscountMoney())- (op.getAutoCommissionMoney()==null?0:op.getAutoCommissionMoney()));
                rr.setVerifyFlag("0");
                rr.setOptUsername("客户自主");
                rr.setOptName("客户自主");
                rr.setAgentPhone(orders.getAgentPhone());
                rr.setAgentOpenid(orders.getAgentOpenid());
                rr.setAgentName(orders.getAgentName());
                rr.setProId(op.getProId());
                rr.setOrdersProId(op.getId());
                rr.setOrdersProTitle(op.getProTitle());
                refundRecordDao.save(rr);

                //申请时还不需要修改退款金额
                /*op.setBackMoney(op.getAmount()*op.getPrice());
                op.setBackLong(System.currentTimeMillis());
                op.setBackTime(NormalTools.curDatetime());
                op.setBackDay(NormalTools.curDate());
                ordersProductDao.save(op); //修改退款金额*/
            }

            //申请时还不需要修改退款金额
            //orders.setBackMoney(orders.getTotalMoney() - (orders.getDiscountMoney() == null ? 0 : orders.getDiscountMoney()));
            orders.setRefundFlag("1"); //设置退款标记为 申请中
            ordersDao.save(orders);

            //TODO 通知管理人员
            sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "退款申请通知", "", "有顾客发起退款申请",
                    TemplateMessageTools.field("订单编号", ordersNo),
                    TemplateMessageTools.field("订单金额", orders.getBackMoney()+" 元"),

                    TemplateMessageTools.field("退款原因："+reason));

            return JsonResult.success("退款申请提交成功");
        } else {
            return JsonResult.success("此订单不能提交退款申请");
        }
    }

    /** 催单 */
    @NeedAuth(openid = true)
    @TemplateMessageAnnotation(name = "催单通知", keys = "订单号-下单时间-收货人-收货人联系方式-收货人地址")
    public JsonResult noticeOrders(String params) {
        try {
            String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
            //TODO 处理崔单业务逻辑
            Orders orders = ordersDao.findByOrdersNo(ordersNo);
            //3小时内不能重复催单
            RemindOrders ro = remindOrdersDao.findByOrdersNoAndTime(ordersNo, System.currentTimeMillis() - 3*3600*1000);
            if(ro==null) {
                ro = new RemindOrders();
                ro.setCreateDay(NormalTools.curDate());
                ro.setCreateLong(System.currentTimeMillis());
                ro.setCreateTime(NormalTools.curDatetime());
                ro.setOrdersId(orders.getId());
                ro.setOrdersNo(ordersNo);
                remindOrdersDao.save(ro);

                CustomAddress ca = customAddressDao.findOne(orders.getAddressId()); //获取地址
                sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "催单通知", "", "有顾客催单了",
                    TemplateMessageTools.field("订单号", ordersNo),
                    TemplateMessageTools.field("下单时间", orders.getPayTime()),
                    TemplateMessageTools.field("收货人", ca.getName()),
                    TemplateMessageTools.field("收货人联系方式", ca.getPhone()),
                    TemplateMessageTools.field("收货人地址", buildAddress(ca)),

                    TemplateMessageTools.field("请核对信息后尽快处理~~"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.success("崔单成功");
    }

    private String buildAddress(CustomAddress ca) {
        StringBuffer sb = new StringBuffer();
        sb.append(ca.getProvinceName()==null?"":ca.getProvinceName())
                .append(ca.getCityName()==null?"":ca.getCityName())
                .append(ca.getCountyName()==null?"":ca.getCountyName())
                .append(ca.getStreet()==null?"":ca.getStreet());
        return sb.toString();
    }

    /** 支付成功后回调此接口 */
    @NeedAuth(openid = true)
    //@TemplateMessageAnnotation(name = "订单付款成功通知", keys = "订单号-支付时间-支付金额-支付方式")
    public JsonResult payRes(String params) {
        try {
            WxCustomDto customDto = JsonTools.getCustom(params);
            String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
            String flag = JsonTools.getJsonParam(params, "flag");
            if("1".equals(flag)) {
                //Float money = Float.parseFloat(JsonTools.getJsonParam(params, "payMoney")); //支付金额
                payTools.hasPayed(ordersNo); //支付成功
            }
            return JsonResult.success("操作成功").set("flag", "1");
        } catch (Exception e) {
            return JsonResult.success("操作失败").set("flag", "0");
        }
    }

    @NeedAuth(openid = true)
    public JsonResult prepay(String params) {
        try {
            WxCustomDto customDto = JsonTools.getCustom(params);
            String ip = JsonTools.getIP(params);
            String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
            PaySubmitDto dto = payTools.unifiedOrder(customDto, ip, ordersNo);
            //System.out.println("-------MiniOrdersService.prepay--"+dto.toString());
            return JsonResult.success("下单成功").set("flag", "1").set("dto", dto);
        } catch (Exception e) {
//            System.out.println("++++++++MiniOrdersService.prepay++"+e.getMessage());
            e.printStackTrace();
            return JsonResult.success("下单失败").set("flag", "0");
        }
    }

    /**
     * 小程序获取订单信息
     * @param params
     * @return
     */
    @NeedAuth(openid = true)
    public JsonResult loadOne(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        Integer id = JsonTools.getId(params); //OrdersId
        Orders orders = ordersDao.findOne(id, customDto.getCustomId());
        List<OrdersProduct> proList = ordersProductDao.findByOrdersId(id);

        QueryTools qt = new QueryTools();
        Page<Product> res = productDao.findAll(qt.buildSearch(new SpecificationOperator("status", "eq", "1", "and"),
                new SpecificationOperator("isRecommend", "eq", "1")),
                SimplePageBuilder.generate(0, 8, SimpleSortBuilder.generateSort("orderNo_a")));

        return JsonResult.success("获取成功").set("orders", orders).set("proList", proList).set("recommendList", res.getContent());
    }

    /** 通过佣金获取订单信息 */
        public JsonResult loadOneByCommission(String params) {
        Integer id = JsonTools.getId(params); //ordersId
        Orders orders = ordersDao.findOne(id);
        List<OrdersExpress> expressList = ordersExpressDao.findByOrdersId(id);
        return JsonResult.success("获取成功").set("orders", orders).set("expressList", expressList);
    }

    /**
     * 小程序中获取订单列表
     * @param params
     * @return
     */
    @NeedAuth(openid = true)
    public JsonResult listOrders(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String status = JsonTools.getJsonParam(params, "status");

        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<Orders> res = ordersDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                new SpecificationOperator("customId", "eq", customDto.getCustomId()),
                new SpecificationOperator("status", "ne", "-10"),//不显示已删除的订单
                (status!=null&&!"".equals(status))?new SpecificationOperator("status", "eq", status, "and"):null),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.getInstance().set("size", (int) res.getTotalElements())
                .set("data", ordersHandlerTools.rebuildOrders(res.getContent()));
    }

    @NeedAuth(openid = true)
    public JsonResult onPay(String params) {
        JsonResult result = JsonResult.getInstance();
        try {
            String ids = JsonTools.getJsonParam(params, "ids");
//            System.out.println(ids);
            String type = JsonTools.getJsonParam(params, "type"); //类型，direct-直接购买；basket-购物车
            Integer addId = JsonTools.getParamInteger(params, "addressId"); //如果是指定收货地址
            WxCustomDto custom = JsonTools.getCustom(params);
            Integer [] proIds ;
            Float totalMoney = 0f;
            List<ProductSpecs> specsList;
            if("basket".equalsIgnoreCase(type)) { //如果是通过购物车购买
                List<ShoppingBasket> basketList = shoppingBasketDao.findByIds(genBasketIds(ids));
                Integer [] specsIds = buildProSpecsIds(basketList); //产品规则ID

                specsList = productSpecsDao.findByIds(specsIds);
                totalMoney = buildTotalMoney(basketList);
                List<Product> proList = productDao.findByIds(buildProIds(basketList)); //产品列表
                List<ProductSpecsDto> resultList = buildDtoListFromBasket(custom.getOpenid(), rebuildBasket(basketList, specsList), proList);
                proIds = buildProIdsByDto(resultList); //产品ID
                List<OrdersCommissionDto> commissionDtoList = commissionTools.buildCommission(custom.getOpenid(), specsIds);
                result.set("productList", resultList).set("commissionList", commissionDtoList);
            } else { //如果是直接购买
                Integer specsId = Integer.parseInt(ids);
                ProductSpecs specs = productSpecsDao.findOne(specsId);
                specsList = new ArrayList<>();
                proIds = new Integer[]{specs.getProId()};
                if(specs.getAmount()>0) {
                    Product pro = productDao.findOne(specs.getProId());
                    List<ProductSpecsDto> dtoList = buildDtoList(custom.getOpenid(), specs, pro);
                    specsList.add(specs);
                    totalMoney = specs.getPrice();

                    /*
                    List<Product> proList = productDao.findByIds(proIds);*/
                    List<OrdersCommissionDto> commissionDtoList = commissionTools.buildCommission(custom.getOpenid(), specsId);
                    result.set("productList", dtoList).set("commissionList", commissionDtoList);
                } else {
                    result.set("productList", new ArrayList<>());
                }
            }

            CustomAddress address = null;
            if(addId!=null&&addId>0) {
                address = customAddressDao.findByCustomIdAndId(custom.getCustomId(), addId);
            }
            if(address==null) {
                address = customAddressDao.findDefaultAddress(custom.getCustomId()); //默认地址
            }

            List<CustomCoupon> couponList = genCoupon(custom.getCustomId(), totalMoney, proIds); //满足条件的优惠券

            result.set("specsList", specsList).set("address", address)
                .set("totalMoney", totalMoney).set("couponList", couponList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 提交订单
     * @param params
     * @return
     */
    @NeedAuth(openid = true)
    public JsonResult submitOrders(String params) {
        JsonResult result = JsonResult.getInstance();
        WxCustomDto custom = JsonTools.getCustom(params);
        //提交的数据对象
        SubmitOrdersDto objDto = JSONObject.toJavaObject(JSON.parseObject(params), SubmitOrdersDto.class);
        /*Integer addressId = JsonTools.getParamInteger(params, "addressId"); //地址ID
        Integer couponId = JsonTools.getParamInteger(params, "couponId"); //优惠券ID
        String remark = JsonTools.getJsonParam(params, "remark");
        String productData = JsonTools.getJsonParam(params, "productData"); //提交的产品ID，_23-89-8_20-82-3_*/

        rabbitNormalTools.updateData("ordersHandlerTools", "addOrders", custom, objDto);
        return result.set("ordersKey", objDto.getOrdersKey());
    }

    @NeedAuth(openid = true)
    public JsonResult queryOrdersNo(String params) {
        String ordersKey = JsonTools.getJsonParam(params, "ordersKey");
        WxCustomDto customDto = JsonTools.getCustom(params);
        String ordersNo = ordersDao.queryOrdersNo(ordersKey, customDto.getCustomId());
        boolean suc = (ordersNo!=null && !"".equals(ordersNo.trim())) ;
        return JsonResult.success("获取成功").set("flag", suc?"1":"0").set("ordersNo", ordersNo);
    }

    private List<ProductSpecsDto> buildDtoListFromBasket(String openid, List<ShoppingBasket> basketList, List<Product> proList) {
        List<ProductSpecsDto> result = new ArrayList<>();
        for(ShoppingBasket sb : basketList) {
            Product pro = getPro(proList, sb.getProId()); //获取产品对象
            ProductSpecsDto dto = new ProductSpecsDto();
            dto.setAmount(sb.getAmount());
            dto.setPrice(sb.getPrice());
            dto.setProId(sb.getProId());
            dto.setProImg(sb.getProImg());
            dto.setProTitle(sb.getProTitle());
            dto.setSpecsId(sb.getSpecsId());
            dto.setSpecsName(sb.getSpecsName());
            dto.setKey(buildKey(openid, sb.getSpecsId(), sb.getProId()));
            dto.setType("basket");
            if(pro!=null && "1".equals(pro.getStatus())) { //如果产品为空或status不为1，则表示此产品不存在或隐藏，则不能购买
                dto.setSaleMode(pro.getSaleMode());
                dto.setDeliveryDate(pro.getDeliveryDate());
                result.add(dto);
            }
        }
        return result;
    }

    private Product getPro(List<Product> proList, Integer proId) {
        Product pro = null;
        for(Product p : proList) {
            if(p.getId().equals(proId)) {pro = p; break;}
        }
        return pro;
    }

    private List<ProductSpecsDto> buildDtoList(String openid, ProductSpecs specs, Product pro) {
        List<ProductSpecsDto> result = new ArrayList<>();
        if(pro!=null && "1".equals(pro.getStatus())) {
            ProductSpecsDto dto = new ProductSpecsDto();
            dto.setAmount(1);
            dto.setPrice(specs.getPrice());
            dto.setProId(pro.getId());
            dto.setProImg(pro.getHeadImgUrl());
            dto.setProTitle(pro.getTitle());
            dto.setSpecsId(specs.getId());
            dto.setSpecsName(specs.getName());
            dto.setKey(buildKey(openid, specs.getId(), pro.getId()));
            dto.setSaleMode(pro.getSaleMode());
            dto.setDeliveryDate(pro.getDeliveryDate());
            dto.setType("direct");
            result.add(dto);
        }
        return result;
    }

    /** 生成key */
    private String buildKey(String customOpenid, Integer specsId, Integer proId) {
        try {
            String ids = proId+"-"+specsId;
            String pwd = SecurityUtil.md5(customOpenid, ids);
            return pwd;
        } catch (Exception e) {
            return (Math.random()*1000000)+"";
        }
    }

    /**
     * 重新生成购物车数据，解决购物车产品数量大于产品实际库存的问题
     * @param basketList
     * @param specsList
     * @return
     */
    private List<ShoppingBasket> rebuildBasket(List<ShoppingBasket> basketList, List<ProductSpecs> specsList) {
        List<ShoppingBasket> result = new ArrayList<>();
        for(ShoppingBasket sb: basketList) {
            for(ProductSpecs p : specsList) {
                if(sb.getProId().equals(p.getProId()) && sb.getAmount()>p.getAmount()) {
                    sb.setAmount(p.getAmount());
                }
            }
            if(sb.getAmount()>0) {
                result.add(sb);
            }
        }
        return result;
    }

    /** 统计订单总金额 */
    private Float buildTotalMoney(List<ShoppingBasket> basketList) {
        Float res = 0f;
        for(ShoppingBasket sb : basketList) {
            res += (sb.getPrice()*sb.getAmount());
        }
        return res;
    }

    /** 获取优惠券 */
    private List<CustomCoupon> genCoupon(Integer customId, Float totalMoney, Integer [] proIds) {
        List<CustomCoupon> couponList = customCouponDao.findByCanUse(customId, totalMoney, proIds);
        return couponList;
    }

    /** 构建产品规格ID */
    private Integer [] buildProSpecsIds(List<ShoppingBasket> basketList) {
        Integer [] res = new Integer[basketList.size()];
        Integer index = 0;
        for(ShoppingBasket sb : basketList) {
            res[index++] = sb.getSpecsId();
        }
        return res;
    }

    /** 构建产品ID */
    private Integer [] buildProIds(List<ShoppingBasket> basketList) {
        List<Integer> list = new ArrayList<>();
        for(ShoppingBasket sb : basketList) {
            if(!list.contains(sb.getProId())) {list.add(sb.getProId());}
        }
        Integer [] res = new Integer[list.size()+1];
        res[0] = 0; //默认增加一个，否则当没有数据时会报错
        for(int i=0;i<list.size();i++) {res[i+1] = list.get(i);}
        return res;
    }

    /** 构建产品ID */
    private Integer [] buildProIdsByDto(List<ProductSpecsDto> dtoList) {
        List<Integer> list = new ArrayList<>();
        for(ProductSpecsDto psd : dtoList) {
            if(!list.contains(psd.getProId())) {list.add(psd.getProId());}
        }
        Integer [] res = new Integer[list.size()+1];
        res[0] = 0; //默认增加一个，否则当没有数据时会报错
        for(int i=0;i<list.size();i++) {res[i+1] = list.get(i);}
        return res;
    }

    private Integer [] genBasketIds(String ids) {
        if(ids==null) {return new Integer[0];}
        String [] array = ids.split("_");
        List<Integer> list = new ArrayList<>();
        for(String str:array) {
            try {
                Integer i = Integer.parseInt(str);
                list.add(i);
            } catch (Exception e) {
            }
        }
        return buildIds(list);
    }

    private Integer [] buildIds(List<Integer> list) {
        Integer [] res = new Integer[list.size()];
        Integer index = 0;
        for(Integer d : list) {
            res[index++] = d;
        }
        return res;
    }
}
