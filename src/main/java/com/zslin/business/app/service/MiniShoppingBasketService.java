package com.zslin.business.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.dao.IProductDao;
import com.zslin.business.dao.IProductSpecsDao;
import com.zslin.business.dao.IShoppingBasketDao;
import com.zslin.business.model.ProductSpecs;
import com.zslin.business.model.ShoppingBasket;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.rabbit.RabbitMQConfig;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MiniShoppingBasketService {

    @Autowired
    private IShoppingBasketDao shoppingBasketDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private IProductSpecsDao productSpecsDao;

    /**
     * 添加到购物车
     * @param params
     * @return
     */
    public JsonResult add2Basket(String params) {
//        System.out.println(params);
        WxCustomDto custom = JsonTools.getCustom(params);
        ShoppingBasket basket = JSONObject.toJavaObject(JSON.parseObject(params), ShoppingBasket.class);
        basket.setOpenid(custom.getOpenid());
        basket.setUnionid(custom.getUnionid());
        basket.setNickname(custom.getNickname());
        basket.setCustomId(custom.getCustomId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.DIRECT_ROUTING, basket);
        return JsonResult.success("添加成功");
    }

    /**
     * 购物车列表
     * @param params
     * @return
     */
    public JsonResult listBasket(String params) {
        WxCustomDto custom = JsonTools.getCustom(params);
        Sort sort = SimpleSortBuilder.generateSort("id_d");
        List<ShoppingBasket> list = shoppingBasketDao.findByOpenid(custom.getOpenid(), sort);
        List<ProductSpecs> specsList = querySpecs(list);
        return JsonResult.success().set("basketList", list).set("specsList", specsList);
    }

    private List<ProductSpecs> querySpecs(List<ShoppingBasket> basketList) {
        List<Integer> specsIds = new ArrayList<>();
        for(ShoppingBasket basket : basketList) {
            if(!specsIds.contains(basket.getSpecsId())) {specsIds.add(basket.getSpecsId());}
        }
        return productSpecsDao.findByIds(specsIds.toArray(new Integer[]{}));
    }

    /**
     * 修改单条购物车中的数量
     * @param params
     * @return
     */
    public JsonResult updateAmount(String params) {
        JsonResult result = JsonResult.success("操作成功");
        try {
            Integer id = JsonTools.getId(params);
            Integer surplusCount = shoppingBasketDao.querySpecsAmount(id); //产品库存
            surplusCount = surplusCount==null?0:surplusCount;
            Integer amount = JsonTools.getParamInteger(params, "amount");
            if(amount>surplusCount) {
                result.set("isOut", true); //超过库存
                amount = surplusCount;
            } else {
                result.set("isOut", false);
            }
            shoppingBasketDao.updateAmount(amount, id);
            result.set("amount", amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除购物车
     * @param params
     * @return
     */
    public JsonResult deleteBasket(String params) {
        System.out.println(params);
        String idStr = JsonTools.getJsonParam(params, "ids");
        JSONArray jsonArray = JsonTools.str2JsonArray(idStr);
        Integer [] ids = new Integer[jsonArray.size()];
        for(int i=0;i<jsonArray.size();i++) {
            ids[i] = (Integer)jsonArray.get(i);
        }
        shoppingBasketDao.deleteBasket(ids);
        return JsonResult.success("删除成功");
    }
}
