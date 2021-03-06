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

    /** ???????????? */
    public JsonResult afterSale(String params) {
//System.out.println("-------------MiniOrdersService.afterSale-----------"+params);


        //TODO ???????????????????????????
        LoginUserDto userDto = JsonTools.getUser(params);
        //String beanName, String methodName
//        rabbitNormalTools.updateData("payTools", "refund", orders, ordersProduct, userDto, money, reason);
//        payTools.refund(orders, ordersProduct, userDto, money, reason);
        RefundDto dto = payTools.refund(params, userDto);

        return JsonResult.success("????????????").set("res", dto);
    }

    /**
     * ????????????
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

            customCommissionRecordDao.updateStatusNoBatchNo("2", ordersNo); //??????????????????
            return JsonResult.success("????????????").set("flag", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.success("????????????").set("flag", "0");
    }

    /** ???????????? */
    @NeedAuth(openid = true)
    public JsonResult removeOrders(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
        Orders orders = ordersDao.findByOrdersNoAndCustomId(ordersNo, customDto.getCustomId());
        String status = orders.getStatus();
        if("0".equals(status) || "-1".equals(status)) {
            //????????????
//            ordersDao.updateStatus("-10", ordersNo, customDto.getCustomId());
            orders.setStatus("-10");
            ordersDao.save(orders);
            return JsonResult.success("??????????????????");
        } else {
            return JsonResult.success("?????????????????????");
        }
    }

    /** ???????????? - ???????????? */
    @NeedAuth(openid = true)
    @TemplateMessageAnnotation(name = "??????????????????", keys = "????????????-????????????")
    public JsonResult refundOrders(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
        String reason = JsonTools.getJsonParam(params, "reason");
        Orders orders = ordersDao.findByOrdersNoAndCustomId(ordersNo, customDto.getCustomId());
        String flag = orders.getRefundFlag();
        if(flag==null || "".equals(flag) || "0".equals(flag) || "-1".equals(flag)) { //????????? ???????????????????????? ????????????
            //????????????
//            ordersDao.updateStatus("-10", ordersNo, customDto.getCustomId());
//            orders.setStatus("-10");

            String refundNo = orders.getId()+"-"+ RandomTools.genCodeNew();

            //??????????????????
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
                rr.setOptUsername("????????????");
                rr.setOptName("????????????");
                rr.setAgentPhone(orders.getAgentPhone());
                rr.setAgentOpenid(orders.getAgentOpenid());
                rr.setAgentName(orders.getAgentName());
                rr.setProId(op.getProId());
                rr.setOrdersProId(op.getId());
                rr.setOrdersProTitle(op.getProTitle());
                refundRecordDao.save(rr);

                //???????????????????????????????????????
                /*op.setBackMoney(op.getAmount()*op.getPrice());
                op.setBackLong(System.currentTimeMillis());
                op.setBackTime(NormalTools.curDatetime());
                op.setBackDay(NormalTools.curDate());
                ordersProductDao.save(op); //??????????????????*/
            }

            //???????????????????????????????????????
            //orders.setBackMoney(orders.getTotalMoney() - (orders.getDiscountMoney() == null ? 0 : orders.getDiscountMoney()));
            orders.setRefundFlag("1"); //????????????????????? ?????????
            ordersDao.save(orders);

            //TODO ??????????????????
            sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "??????????????????", "", "???????????????????????????",
                    TemplateMessageTools.field("????????????", ordersNo),
                    TemplateMessageTools.field("????????????", orders.getBackMoney()+" ???"),

                    TemplateMessageTools.field("???????????????"+reason));

            return JsonResult.success("????????????????????????");
        } else {
            return JsonResult.success("?????????????????????????????????");
        }
    }

    /** ?????? */
    @NeedAuth(openid = true)
    @TemplateMessageAnnotation(name = "????????????", keys = "?????????-????????????-?????????-?????????????????????-???????????????")
    public JsonResult noticeOrders(String params) {
        try {
            String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
            //TODO ????????????????????????
            Orders orders = ordersDao.findByOrdersNo(ordersNo);
            //3???????????????????????????
            RemindOrders ro = remindOrdersDao.findByOrdersNoAndTime(ordersNo, System.currentTimeMillis() - 3*3600*1000);
            if(ro==null) {
                ro = new RemindOrders();
                ro.setCreateDay(NormalTools.curDate());
                ro.setCreateLong(System.currentTimeMillis());
                ro.setCreateTime(NormalTools.curDatetime());
                ro.setOrdersId(orders.getId());
                ro.setOrdersNo(ordersNo);
                remindOrdersDao.save(ro);

                CustomAddress ca = customAddressDao.findOne(orders.getAddressId()); //????????????
                sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "????????????", "", "??????????????????",
                    TemplateMessageTools.field("?????????", ordersNo),
                    TemplateMessageTools.field("????????????", orders.getPayTime()),
                    TemplateMessageTools.field("?????????", ca.getName()),
                    TemplateMessageTools.field("?????????????????????", ca.getPhone()),
                    TemplateMessageTools.field("???????????????", buildAddress(ca)),

                    TemplateMessageTools.field("??????????????????????????????~~"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.success("????????????");
    }

    private String buildAddress(CustomAddress ca) {
        StringBuffer sb = new StringBuffer();
        sb.append(ca.getProvinceName()==null?"":ca.getProvinceName())
                .append(ca.getCityName()==null?"":ca.getCityName())
                .append(ca.getCountyName()==null?"":ca.getCountyName())
                .append(ca.getStreet()==null?"":ca.getStreet());
        return sb.toString();
    }

    /** ?????????????????????????????? */
    @NeedAuth(openid = true)
    //@TemplateMessageAnnotation(name = "????????????????????????", keys = "?????????-????????????-????????????-????????????")
    public JsonResult payRes(String params) {
        try {
            WxCustomDto customDto = JsonTools.getCustom(params);
            String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
            String flag = JsonTools.getJsonParam(params, "flag");
            if("1".equals(flag)) {
                //Float money = Float.parseFloat(JsonTools.getJsonParam(params, "payMoney")); //????????????
                payTools.hasPayed(ordersNo); //????????????
            }
            return JsonResult.success("????????????").set("flag", "1");
        } catch (Exception e) {
            return JsonResult.success("????????????").set("flag", "0");
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
            return JsonResult.success("????????????").set("flag", "1").set("dto", dto);
        } catch (Exception e) {
//            System.out.println("++++++++MiniOrdersService.prepay++"+e.getMessage());
            e.printStackTrace();
            return JsonResult.success("????????????").set("flag", "0");
        }
    }

    /**
     * ???????????????????????????
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

        return JsonResult.success("????????????").set("orders", orders).set("proList", proList).set("recommendList", res.getContent());
    }

    /** ?????????????????????????????? */
        public JsonResult loadOneByCommission(String params) {
        Integer id = JsonTools.getId(params); //ordersId
        Orders orders = ordersDao.findOne(id);
        List<OrdersExpress> expressList = ordersExpressDao.findByOrdersId(id);
        return JsonResult.success("????????????").set("orders", orders).set("expressList", expressList);
    }

    /**
     * ??????????????????????????????
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
                new SpecificationOperator("status", "ne", "-10"),//???????????????????????????
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
            String type = JsonTools.getJsonParam(params, "type"); //?????????direct-???????????????basket-?????????
            Integer addId = JsonTools.getParamInteger(params, "addressId"); //???????????????????????????
            WxCustomDto custom = JsonTools.getCustom(params);
            Integer [] proIds ;
            Float totalMoney = 0f;
            List<ProductSpecs> specsList;
            if("basket".equalsIgnoreCase(type)) { //??????????????????????????????
                List<ShoppingBasket> basketList = shoppingBasketDao.findByIds(genBasketIds(ids));
                Integer [] specsIds = buildProSpecsIds(basketList); //????????????ID

                specsList = productSpecsDao.findByIds(specsIds);
                totalMoney = buildTotalMoney(basketList);
                List<Product> proList = productDao.findByIds(buildProIds(basketList)); //????????????
                List<ProductSpecsDto> resultList = buildDtoListFromBasket(custom.getOpenid(), rebuildBasket(basketList, specsList), proList);
                proIds = buildProIdsByDto(resultList); //??????ID
                List<OrdersCommissionDto> commissionDtoList = commissionTools.buildCommission(custom.getOpenid(), specsIds);
                result.set("productList", resultList).set("commissionList", commissionDtoList);
            } else { //?????????????????????
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
                address = customAddressDao.findDefaultAddress(custom.getCustomId()); //????????????
            }

            List<CustomCoupon> couponList = genCoupon(custom.getCustomId(), totalMoney, proIds); //????????????????????????

            result.set("specsList", specsList).set("address", address)
                .set("totalMoney", totalMoney).set("couponList", couponList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ????????????
     * @param params
     * @return
     */
    @NeedAuth(openid = true)
    public JsonResult submitOrders(String params) {
        JsonResult result = JsonResult.getInstance();
        WxCustomDto custom = JsonTools.getCustom(params);
        //?????????????????????
        SubmitOrdersDto objDto = JSONObject.toJavaObject(JSON.parseObject(params), SubmitOrdersDto.class);
        /*Integer addressId = JsonTools.getParamInteger(params, "addressId"); //??????ID
        Integer couponId = JsonTools.getParamInteger(params, "couponId"); //?????????ID
        String remark = JsonTools.getJsonParam(params, "remark");
        String productData = JsonTools.getJsonParam(params, "productData"); //???????????????ID???_23-89-8_20-82-3_*/

        rabbitNormalTools.updateData("ordersHandlerTools", "addOrders", custom, objDto);
        return result.set("ordersKey", objDto.getOrdersKey());
    }

    @NeedAuth(openid = true)
    public JsonResult queryOrdersNo(String params) {
        String ordersKey = JsonTools.getJsonParam(params, "ordersKey");
        WxCustomDto customDto = JsonTools.getCustom(params);
        String ordersNo = ordersDao.queryOrdersNo(ordersKey, customDto.getCustomId());
        boolean suc = (ordersNo!=null && !"".equals(ordersNo.trim())) ;
        return JsonResult.success("????????????").set("flag", suc?"1":"0").set("ordersNo", ordersNo);
    }

    private List<ProductSpecsDto> buildDtoListFromBasket(String openid, List<ShoppingBasket> basketList, List<Product> proList) {
        List<ProductSpecsDto> result = new ArrayList<>();
        for(ShoppingBasket sb : basketList) {
            Product pro = getPro(proList, sb.getProId()); //??????????????????
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
            if(pro!=null && "1".equals(pro.getStatus())) { //?????????????????????status??????1?????????????????????????????????????????????????????????
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

    /** ??????key */
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
     * ??????????????????????????????????????????????????????????????????????????????????????????
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

    /** ????????????????????? */
    private Float buildTotalMoney(List<ShoppingBasket> basketList) {
        Float res = 0f;
        for(ShoppingBasket sb : basketList) {
            res += (sb.getPrice()*sb.getAmount());
        }
        return res;
    }

    /** ??????????????? */
    private List<CustomCoupon> genCoupon(Integer customId, Float totalMoney, Integer [] proIds) {
        List<CustomCoupon> couponList = customCouponDao.findByCanUse(customId, totalMoney, proIds);
        return couponList;
    }

    /** ??????????????????ID */
    private Integer [] buildProSpecsIds(List<ShoppingBasket> basketList) {
        Integer [] res = new Integer[basketList.size()];
        Integer index = 0;
        for(ShoppingBasket sb : basketList) {
            res[index++] = sb.getSpecsId();
        }
        return res;
    }

    /** ????????????ID */
    private Integer [] buildProIds(List<ShoppingBasket> basketList) {
        List<Integer> list = new ArrayList<>();
        for(ShoppingBasket sb : basketList) {
            if(!list.contains(sb.getProId())) {list.add(sb.getProId());}
        }
        Integer [] res = new Integer[list.size()+1];
        res[0] = 0; //??????????????????????????????????????????????????????
        for(int i=0;i<list.size();i++) {res[i+1] = list.get(i);}
        return res;
    }

    /** ????????????ID */
    private Integer [] buildProIdsByDto(List<ProductSpecsDto> dtoList) {
        List<Integer> list = new ArrayList<>();
        for(ProductSpecsDto psd : dtoList) {
            if(!list.contains(psd.getProId())) {list.add(psd.getProId());}
        }
        Integer [] res = new Integer[list.size()+1];
        res[0] = 0; //??????????????????????????????????????????????????????
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
