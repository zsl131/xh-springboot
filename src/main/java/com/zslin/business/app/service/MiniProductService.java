package com.zslin.business.app.service;

import com.zslin.business.app.dto.PriceDto;
import com.zslin.business.app.tools.PriceTools;
import com.zslin.business.dao.*;
import com.zslin.business.model.*;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.rabbit.RabbitNormalTools;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.repository.SpecificationOperator;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 小程序端 - 产品
 */
@Service
public class MiniProductService {

    @Autowired
    private IProductDao productDao;

    @Autowired
    private IProductSpecsDao productSpecsDao;

    @Autowired
    private IMediumDao mediumDao;

    @Autowired
    private IProductFavoriteRecordDao productFavoriteRecordDao;

    @Autowired
    private RabbitNormalTools rabbitNormalTools;

    @Autowired
    private IShoppingBasketDao shoppingBasketDao;

    @Autowired
    private ICouponDao couponDao;

    public JsonResult loadOne(String params) {
        try {
//            String ip = JsonTools.getIP(params);

//            System.out.println("---MiniProductService IP::---"+ip);

            Integer id = JsonTools.getId(params);
            Product product = productDao.findOne(id);
            Sort sort = SimpleSortBuilder.generateSort("orderNo");
            //获取有库存的规格
            List<ProductSpecs> specsList = productSpecsDao.findByProIdAndAmount(id, sort);
            List<Medium> mediumList = mediumDao.findByObjClassNameAndObjIdAndStatus("Product", id, "1", sort);
            List<Medium> mediumList2 = new ArrayList<>();
            for(Medium m : mediumList) {
                if(m.getOrderNo()<=5) {mediumList2.add(m);} //前5个为产品详情面顶部轮播图
            }
            PriceDto priceDto = PriceTools.buildPriceDto(specsList);

            WxCustomDto custom = JsonTools.getCustom(params);
            ProductFavoriteRecord pfr = productFavoriteRecordDao.findByProIdAndCustomId(id, custom.getCustomId());
//        productDao.plusReadCount(1, id); //增加点击量

            Integer basketCount = shoppingBasketDao.queryCount(custom.getOpenid());
            plusCount(id); //增加点击量

            /*//优惠券
            List<Coupon> couponList = couponDao.findByProId(id);*/

            return JsonResult.success("获取成功").set("product", product).set("specsList", specsList)
                    .set("mediumList", mediumList2).set("price", priceDto).set("favorite", pfr)
                    .set("basketCount", basketCount==null?0:basketCount);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error("获取出错", e.getMessage());
        }
    }

    public JsonResult list(String params) {
        String type = JsonTools.getJsonParam(params, "type"); //获取的类型，1-当季；2-预售

        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<Product> res = productDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                new SpecificationOperator("status", "eq", "1"),
                (type==null||"".equals(type) ||"0".equals(type))?null:new SpecificationOperator("saleMode", "eq", type)),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));
//        List<Integer> favoriteIds = productFavoriteRecordDao.findIdsByCustomId(custom.getCustomId());
        return JsonResult.success().set("size", res.getTotalElements()).set("data", res.getContent());//.set("favoriteIds", favoriteIds);
    }

    private void plusCount(Integer proId) {
        rabbitNormalTools.updateData("productDao", "plusReadCount", 1, proId);
    }
}
