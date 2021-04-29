package com.zslin.business.app.tools;

import com.zslin.business.app.dto.OrdersCommissionDto;
import com.zslin.business.app.dto.SubmitOrdersDto;
import com.zslin.business.app.dto.orders.OrdersHandlerDto;
import com.zslin.business.app.dto.orders.OrdersProductDto;
import com.zslin.business.app.dto.orders.OrdersRateDto;
import com.zslin.business.dao.*;
import com.zslin.business.dto.OrdersShowDto;
import com.zslin.business.mini.tools.MiniUtils;
import com.zslin.business.model.*;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.WxCustomDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 订单处理工具类
 *
 */
@Component("ordersHandlerTools")
@HasTemplateMessage
public class OrdersHandlerTools {

    @Autowired
    private ICustomAddressDao customAddressDao;

    @Autowired
    private ICustomCouponDao customCouponDao;

    @Autowired
    private IProductDao productDao;

    @Autowired
    private IProductSpecsDao productSpecsDao;

    @Autowired
    private IOrdersDao ordersDao;

    @Autowired
    private IOrdersProductDao ordersProductDao;

    @Autowired
    private IAgentDao agentDao;

    @Autowired
    private IOrdersCouponDao ordersCouponDao;

    @Autowired
    private IAgentLevelDao agentLevelDao;

    @Autowired
    private ICustomCommissionRecordDao customCommissionRecordDao;

    @Autowired
    private RateTools rateTools;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @Autowired
    private ICustomerDao customerDao;

    @Autowired
    private CommissionTools commissionTools;

    @Transactional
    @TemplateMessageAnnotation(name = "订单创建成功通知", keys = "订单号-商品数量-商品金额")
    public void addOrders(WxCustomDto custom, SubmitOrdersDto ordersDto) {
        Orders oldOrders = ordersDao.findByOrdersKey(ordersDto.getOrdersKey());
        if(oldOrders!=null) {return;} //如果订单已经存在，则不能再操作
//        System.out.println(custom);
//        System.out.println("------------------------------");
        //SubmitOrdersDto(ordersKey=1_442098271, addressId=5, agentId=0 couponId=0, remark=, productData=_23-89-8_20-82-3_)
        CustomCoupon coupon = null; //优惠券
        CustomAddress address = null; //收货地址
        Agent agent = null; //对应代理
        AgentLevel level = null; //代理对应的代理等级对象
        if(ordersDto.getCouponId()!=null && ordersDto.getCouponId()>0) {coupon = customCouponDao.findOne(ordersDto.getCouponId());}
        if(ordersDto.getAddressId()!=null && ordersDto.getAddressId()>0) {address = customAddressDao.findOne(ordersDto.getAddressId());}
//        if(ordersDto.getAgentId()!=null && ordersDto.getAgentId()>0) {agent = agentDao.findOne(ordersDto.getAgentId());} //由于前端获取的是Customer，所以不能通过agentId获取对象
        if(ordersDto.getAgentOpenid()!=null && !"".equals(ordersDto.getAgentOpenid())) {agent = agentDao.findByOpenid(ordersDto.getAgentOpenid());} //通过Openid获取对象
        if(agent==null) {agent = agentDao.findOkByOpenid(custom.getOpenid());} //如果没有其他代理，则获取自身代理
        if(agent!=null && agent.getLevelId()!=null && agent.getLevelId()>0) {level = agentLevelDao.findOne(agent.getLevelId());}

        String ordersKey = ordersDto.getOrdersKey();
        String ordersNo = buildOrdersNo(custom.getCustomId());
        //产品信息列表
        List<OrdersProductDto> productDtoList = generateProducts(ordersDto.getProductData());

        boolean isSelf = agent!=null && (custom.getOpenid().equals(agent.getOpenid())); //是否代理就是客户自己
//    System.out.println(agent+"------------------->"+isSelf);
        //如果代理是客户本身，则计算佣金金额
        List<OrdersCommissionDto> commissionDtoList = isSelf?commissionTools.buildCommission(agent, buildSpecsIds(productDtoList)):null;

        List<CustomCommissionRecord> commissionRecordList = buildCommission(agent, custom, level, productDtoList,
                ordersKey, ordersNo, isSelf, commissionDtoList);

        String proTitles = buildProTitles(productDtoList);

        OrdersHandlerDto countDto = buildHandlerDto(productDtoList, commissionRecordList);

        //订单自动抵扣的佣金总金额
        Float commissionTotalMoney = buildTotalCommission(productDtoList, commissionDtoList);

//        System.out.println(ordersDto);
        Orders orders = addOrders(ordersKey, ordersNo, custom, address, agent, coupon, countDto, ordersDto.getRemark(),
                proTitles, commissionTotalMoney);
        //订单生成后要处理用户优惠券
        buildCoupon(orders, coupon);
        //保存佣金，
        for(CustomCommissionRecord ccr : commissionRecordList) {
            ccr.setOrdersId(orders.getId());
            customCommissionRecordDao.save(ccr);
        }
        //保存订单产品
        saveOrderProducts(orders, agent, level, custom, productDtoList, commissionDtoList);

        Float discountMoney = orders.getDiscountMoney();
        discountMoney = (discountMoney == null) ? 0 : discountMoney;

        sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "订单创建成功通知", "", proTitles,
                TemplateMessageTools.field("订单号", ordersNo),
                TemplateMessageTools.field("商品件数", orders.getSpecsCount()+" 件"),
                TemplateMessageTools.field("商品金额", (orders.getTotalMoney()-discountMoney)+" 元"),

                TemplateMessageTools.field("可以前往后台管理系统查看["+ MiniUtils.buildAgent(orders)+"]"));
    }

    /** 计算订单总佣金金额 */
    private Float buildTotalCommission(List<OrdersProductDto> productDtoList, List<OrdersCommissionDto> commissionDtoList) {
        Float result = 0f;
        if(productDtoList==null || productDtoList.size()<=0 || commissionDtoList==null || commissionDtoList.size()<=0) {return result;}
        for(OrdersProductDto opd : productDtoList) {
            result += (buildCommission(commissionDtoList, opd));
        }
        return result;
    }

    /** 获取订单产品的佣金金额 */
    private Float buildCommission(List<OrdersCommissionDto> commissionDtoList, OrdersProductDto opd) {
        Float result = 0f;
        if(commissionDtoList==null || commissionDtoList.size()<=0) {return result;}

        for(OrdersCommissionDto ocd : commissionDtoList) {
            if(ocd.getSpecsId().equals(opd.getSpecs().getId())) {result = ocd.getRate() * opd.getAmount(); break;}
        }
        return result;
    }

    private Orders addOrders(String ordersKey, String ordersNo, WxCustomDto custom, CustomAddress address,
                           Agent agent, CustomCoupon coupon, OrdersHandlerDto countDto, String remark,
                             String proTitles, Float commissionTotalMoney) {
        Orders order = new Orders();
        order.setAddressCon(buildAddressCon(address));
        order.setAddressId(address.getId());
        order.setRemark(remark);
        order.setProTitles(proTitles);
        order.setHasAgent("0"); //默认为没有代理
        order.setAutoCommissionMoney(commissionTotalMoney); //自动抵扣的佣金金额

        if(agent!=null) { //如果有代理信息，则设置
            order.setAgentName(agent.getName());
            order.setAgentOpenid(agent.getOpenid());
            order.setAgentPhone(agent.getPhone());
            order.setAgentUnionid(agent.getUnionid());
            order.setAgentId(agent.getId());
            order.setHasAgent("1"); //设置为有代理
        } else { //如果没有代理信息，则使用默认的代理信息
            try {
                Customer c = customerDao.findByOpenid(custom.getOpenid());
                String inviterOpenid = c.getInviterOpenid();
                if(inviterOpenid!=null && !"".equals(inviterOpenid)) {
                    order.setAgentName(c.getInviterNickname());
                    order.setAgentOpenid(inviterOpenid);
    //                order.setAgentPhone(c.getIn);
    //                order.setAgentUnionid(agent.getUnionid());
                    order.setAgentId(c.getInviterId());
                    order.setDefaultAgent("1"); //使用默认的信息
                }
            } catch (Exception e) {
            }
        }
        order.setCreateDay(NormalTools.curDate());
        order.setCreateTime(NormalTools.curDatetime());
        order.setCreateLong(System.currentTimeMillis());
        order.setCustomId(custom.getCustomId());
        order.setOpenid(custom.getOpenid());
        order.setUnionid(custom.getUnionid());
        order.setNickname(custom.getNickname());
        order.setHeadImgUrl(custom.getHeadImgUrl());
        order.setStatus("0");
        order.setHasAfterSale("0"); //默认为无售后问题
        order.setOrdersKey(ordersKey);
        order.setOrdersNo(ordersNo);
        order.setFreight(0f); //TODO 设置运费
        if(coupon!=null) {
            order.setDiscountMoney(coupon.getWorth()); //TODO 设置优惠金额
            order.setDiscountReason(coupon.getCouponName()); //TODO 设置优惠原因
        }
        order.setFundMoney(countDto.getFundMoney()); //TODO 设置基金金额
        order.setSpecsCount(countDto.getSpecsCount()); //TODO 设置产品件数
        order.setTotalCommission(countDto.getTotalCommission()); //TODO 设置佣金金额
        order.setTotalCount(countDto.getTotalCount()); //TODO 设置产品总数量
        order.setTotalMoney(countDto.getTotalMoney()); //TODO 设置总金额
        ordersDao.save(order);
        return order;
    }

    /**
     * 构建计数DTO
     * @param dtoList
     * @param commissionRecordList
     * @return
     */
    private OrdersHandlerDto buildHandlerDto(List<OrdersProductDto> dtoList, List<CustomCommissionRecord> commissionRecordList) {
        /** 基金金额 */
        Float fundMoney=0f;
        /** 总件数 */
        Integer specsCount=0;
        /** 总佣金金额 */
        Float totalCommission=0f;
        /** 产品总数量 */
        Integer totalCount=0;
        /** 总金额 */
        Float totalMoney=0f;
        List<Integer> proIdsList = new ArrayList<>();
        for(OrdersProductDto dto:dtoList) {
            if(!proIdsList.contains(dto.getProduct().getId())) {proIdsList.add(dto.getProduct().getId());}
            specsCount += dto.getAmount();
            totalMoney += dto.getSpecs().getPrice()*dto.getAmount();
            fundMoney += (dto.getProduct().getFund()*dto.getAmount());//
        }
        totalCount = proIdsList.size();
        for(CustomCommissionRecord ccr : commissionRecordList) {
            totalCommission += ccr.getMoney();
        }

        return new OrdersHandlerDto(fundMoney, specsCount, totalCommission, totalCount, totalMoney);
    }

    private void saveOrderProducts(Orders order, Agent agent, AgentLevel level, WxCustomDto custom,
                                   List<OrdersProductDto> productDtoList, List<OrdersCommissionDto> commissionDtoList) {
        for(OrdersProductDto dto:productDtoList) {
            Product pro = dto.getProduct();
            OrdersProduct op = new OrdersProduct();
            if(level!=null) {
                op.setAgentLevelId(level.getId());
                op.setAgentLevelName(level.getName());
            }
            if(agent!=null) {
                op.setAgentOpenid(agent.getOpenid());
                op.setAgentUnionid(agent.getUnionid());
                op.setAgentId(agent.getId());
            }
            op.setAutoCommissionMoney(buildCommission(commissionDtoList, dto)); //自动抵扣的佣金金额
            op.setAmount(dto.getAmount());
            op.setCustomId(custom.getCustomId());
            op.setDeliveryDate(pro.getDeliveryDate());
            op.setFund(pro.getFund()*dto.getAmount());
            op.setHasAfterSale("0");
            op.setNickname(custom.getNickname());
            op.setOpenid(custom.getOpenid());
            op.setOrdersId(order.getId());
            op.setOrdersKey(order.getOrdersKey());
            op.setOrdersNo(order.getOrdersNo());
            op.setOriPrice(dto.getSpecs().getOriPrice());
            op.setPrice(dto.getSpecs().getPrice());
            op.setProId(pro.getId());
            op.setProTitle(pro.getTitle());
            op.setSaleMode(pro.getSaleMode());
            op.setProImg(pro.getHeadImgUrl()); //图片
            op.setSpecsId(dto.getSpecs().getId());
            op.setSpecsName(dto.getSpecs().getName());
            op.setUnionid(custom.getUnionid());
            op.setStatus("0");
            ordersProductDao.save(op); //保存
            productDao.plusSaleCount(dto.getAmount(), pro.getId()); //增加销量
            minusSpecsCount(dto.getSpecs().getId(), dto.getAmount()); //减少库存
        }
    }

    /**
     * 减库存
     * @param specsId 规格ID
     * @param amount 数量
     */
    private void minusSpecsCount(Integer specsId, Integer amount) {
        productSpecsDao.minusAmount(amount, specsId);
    }

    /**
     * //TODO 除了当级代理的佣金还有上级代理的佣金
     * 构建佣金记录
     * 添加时要遍历设置ordersId
     * @param agent 代理信息
     * @param custom 客户信息
     * @param level 代理等级
     * @param proDtoList 产品对象列表
     * @param ordersKey 订单Key
     * @param ordersNo 订单编号
     * @return
     */
    private List<CustomCommissionRecord> buildCommission(Agent agent, WxCustomDto custom, AgentLevel level,
                                                         List<OrdersProductDto> proDtoList, String ordersKey, String ordersNo,
                                                         boolean isSelf, List<OrdersCommissionDto> commissionDtoList) {
        List<CustomCommissionRecord> result = new ArrayList<>();
        if(agent==null || level==null) {return result;}
        /*boolean isSelf = custom.getOpenid().equals(agent.getOpenid()); //是否代理就是客户自己
        List<OrdersCommissionDto> commissionDtoList = commissionTools.buildCommission(agent, buildSpecsIds(proDtoList));*/
        Agent leaderAgent = null;
        if(agent.getLeaderId()!=null && agent.getLeaderId()>0) {leaderAgent = agentDao.findOne(agent.getLeaderId());} //获取上级代理
        for(OrdersProductDto proDto:proDtoList) {
            Float thisMoney = 0f, leaderMoney = 0f;
            if(isSelf) {
                OrdersCommissionDto commissionDto = queryDto(commissionDtoList, proDto.getSpecs().getId());
                thisMoney = commissionDto.getRate(); leaderMoney = commissionDto.getLeaderRate();
            } else {
                OrdersRateDto rateDto = rateTools.getRate(level.getId(), proDto.getSpecs().getId()); //佣金DTO对象
                thisMoney = rateDto.getThisAmount(); leaderMoney = rateDto.getLeaderAmount();
            }

            /*OrdersRateDto rateDto = rateTools.getRate(level.getId(), proDto.getSpecs().getId()); //佣金DTO对象
            result.add(buildRecord(agent, agent, level, custom, rateDto.getThisAmount()*proDto.getAmount(), ordersKey, ordersNo, proDto, commissionDtoList));
            //TODO 如果有上级代理且上级是 “金牌代理【id为3】”，并且自己不是金牌代理，也添加进去
            if(leaderAgent!=null && leaderAgent.getLevelId()==3 && agent.getLevelId()!=3) {
                result.add(buildRecord(agent, leaderAgent, level, custom, rateDto.getLeaderAmount()*proDto.getAmount(), ordersKey, ordersNo, proDto, commissionDtoList));
            }*/

//            OrdersCommissionDto commissionDto = queryDto(commissionDtoList, proDto.getSpecs().getId());
            result.add(buildRecord(agent, agent, level, custom, thisMoney*proDto.getAmount(), ordersKey, ordersNo, proDto, isSelf));
            //TODO 如果有上级代理且上级是 “金牌代理【id为3】”，并且自己不是金牌代理，也添加进去
            if(leaderAgent!=null && leaderAgent.getLevelId()==3 && agent.getLevelId()!=3) {
                result.add(buildRecord(agent, leaderAgent, level, custom, leaderMoney*proDto.getAmount(), ordersKey, ordersNo, proDto, false));
            }
        }
        return result;
    }

    private OrdersCommissionDto queryDto(List<OrdersCommissionDto> commissionDtoList, Integer specsId) {
        OrdersCommissionDto res = null;
        if(commissionDtoList==null) {return null;}
        for(OrdersCommissionDto dto : commissionDtoList) {if(specsId.equals(dto.getSpecsId())) {res = dto; break;}}
        return res;
    }

    private Integer[] buildSpecsIds(List<OrdersProductDto> productDtoList) {
        Integer [] res = new Integer[productDtoList.size()];
        for(int i=0;i<productDtoList.size();i++) {
            res[i] = productDtoList.get(i).getSpecs().getId();
        }
        return res;
    }

    private CustomCommissionRecord buildRecord(Agent saler, Agent agent, AgentLevel level, WxCustomDto custom, Float money,
                                               String ordersKey, String ordersNo, OrdersProductDto proDto,
                                               boolean isSelf) {
        CustomCommissionRecord ccr = new CustomCommissionRecord();

        //设置具体的销售人员信息
        ccr.setSalerId(saler.getId());
        ccr.setSalerName(saler.getName());
        ccr.setSalerOpenid(saler.getOpenid());
        ccr.setSalerPhone(saler.getPhone());

        if(saler.getId().equals(agent.getId())) {
            ccr.setHaveType("0"); //说明是自己推广
        } else {ccr.setHaveType("1");} //说明是下级代理推广

        ccr.setAgentId(agent.getId());
        ccr.setAgentLevelId(level.getId());
        ccr.setAgentLevelName(level.getName());
        ccr.setAgentName(agent.getName());
        ccr.setAgentOpenid(agent.getOpenid());
        ccr.setAgentPhone(agent.getPhone());
        ccr.setAgentUnionid(agent.getUnionid());
        ccr.setCreateDay(NormalTools.curDate());
        ccr.setCreateMonth(NormalTools.getNow("yyyyMM"));
        ccr.setCreateLong(System.currentTimeMillis());
        ccr.setCreateTime(NormalTools.curDatetime());
        ccr.setCustomId(agent.getCustomId());
        ccr.setCustomNickname(custom.getNickname());
        ccr.setCustomOpenid(custom.getOpenid());
        ccr.setCustomUnionid(custom.getUnionid());
        ccr.setMoney(money); //TODO 设置佣金
        ccr.setOrdersKey(ordersKey);
        ccr.setOrdersNo(ordersNo);
        ccr.setProId(proDto.getProduct().getId());
        ccr.setProTitle(proDto.getProduct().getTitle());
        ccr.setSpecsId(proDto.getSpecs().getId());
        ccr.setSpecsName(proDto.getSpecs().getName());
        ccr.setSpecsCount(proDto.getAmount());
        ccr.setIsAuto(isSelf?"1":"0"); //是否是自动抵扣
        ccr.setStatus("0"); //默认为0，用户刚下单
        return ccr;
    }

    /**
     * 处理优惠券
     * @param order
     * @param coupon
     */
    private void buildCoupon(Orders order, CustomCoupon coupon) {
        if(coupon!=null) {
            OrdersCoupon oc = new OrdersCoupon();
            oc.setCouponId(coupon.getCouponId());
            oc.setCouponName(coupon.getCouponName());
            oc.setDiscountMoney(coupon.getWorth());
            oc.setOpenid(coupon.getOpenid());
            oc.setOrdersKey(order.getOrdersKey());
            oc.setOrdersNo(order.getOrdersNo());
            oc.setOrdersId(order.getId());
            oc.setUnionid(coupon.getUnionid());
            oc.setUsedDay(NormalTools.curDate());
            oc.setUsedLong(System.currentTimeMillis());
            oc.setUsedTime(NormalTools.curDatetime());
            oc.setCustomCouponId(coupon.getId());
            ordersCouponDao.save(oc);
            customCouponDao.updateStatus("3", coupon.getId()); //设置为已使用
        }
    }

    private List<OrdersProductDto> generateProducts(String productData) {
        List<OrdersProductDto> result = new ArrayList<>();
        String [] array = productData.split("_");
        for(String str : array) {
            if(str!=null && !"".equals(str.trim())) {
                Integer [] ids = getIds(str); //0-产品ID；1-规格ID；2-数量
                if(ids!=null) {
                    Product product = productDao.findOne(ids[0]);
                    ProductSpecs specs = productSpecsDao.findOne(ids[1]); //产品规格
                    Integer amount = ids[2]; //数量
                    result.add(new OrdersProductDto(product, specs, amount));
                }
            }
        }
        return result;
    }

    private Integer [] getIds(String str) {
        String [] ids = str.split("-");
        if(ids.length==3) {
            return new Integer[]{Integer.parseInt(ids[0]), Integer.parseInt(ids[1]), Integer.parseInt(ids[2])};
        } else {return null;} //如果长度不够则过虑；
    }

    private String buildAddressCon(CustomAddress address) {
        StringBuffer sb = new StringBuffer();
        sb.append(address.getName()).append(",")
                .append(address.getProvinceName()==null?"":address.getProvinceName())
                .append(address.getCityName()==null?"":address.getCityName())
                .append(address.getCountyName()==null?"":address.getCountyName())
                .append(address.getStreet()).append(",")
                .append(address.getPhone());
        return sb.toString();
    }

    /**
     * 生成产品标题集合，多标题用｜分隔
     * @param productDtoList
     * @return
     */
    private String buildProTitles(List<OrdersProductDto> productDtoList) {
        StringBuffer sb = new StringBuffer();
        int index = 0;
        for(OrdersProductDto opd : productDtoList) {
            index ++;
            sb.append(opd.getProduct().getTitle());
            if(index<productDtoList.size()) {
                sb.append("｜");
            }
        }
        return sb.toString();
    }

    /**
     * 订单编号规则
     * 前14位是时间，后3位是随机数，中间数字为用户ID
     * @param customId 用户ID
     * @return
     */
    public String buildOrdersNo(Integer customId) {
        String curDate = NormalTools.getNow("yyyyMMddHHmmss")+customId;
        int random = genRandomInt();
        return  curDate+random;
    }

    private Integer genRandomInt() {
        int res = 0;
        Random ran = new Random();
        while(res<100) {
            res = ran.nextInt(999);
        }
        return res;
    }

    /**
     * 为了方便显示，重新构建订单列表
     * @param ordersList
     * @return
     */
    public List<OrdersShowDto> rebuildOrders(List<Orders> ordersList) {
        List<OrdersShowDto> result = new ArrayList<>();
        for(Orders orders : ordersList) {
            result.add(new OrdersShowDto(orders, ordersProductDao.findByOrdersId(orders.getId()),
                    customCommissionRecordDao.findByOrdersId(orders.getId())));
        }
        return result;
    }
}

