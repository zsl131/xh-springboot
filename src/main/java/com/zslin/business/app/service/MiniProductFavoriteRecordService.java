package com.zslin.business.app.service;

import com.zslin.business.dao.IProductFavoriteRecordDao;
import com.zslin.business.model.ProductFavoriteRecord;
import com.zslin.core.common.NormalTools;
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
import org.springframework.stereotype.Service;

@Service
public class MiniProductFavoriteRecordService {

    @Autowired
    private IProductFavoriteRecordDao productFavoriteRecordDao;

    @Autowired
    private RabbitNormalTools rabbitNormalTools;

    public JsonResult list(String params) {
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        WxCustomDto custom = JsonTools.getCustom(params);
        Page<ProductFavoriteRecord> res = productFavoriteRecordDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                new SpecificationOperator("customId", "eq", custom.getCustomId())),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("data", res.getContent());
    }

    public JsonResult addOrDelete(String params) {
        //System.out.println("----->"+params);
        WxCustomDto custom = JsonTools.getCustom(params);
        Integer proId = JsonTools.getParamInteger(params, "proId");
        String proTitle = JsonTools.getJsonParam(params, "proTitle");
        String proImg = JsonTools.getJsonParam(params, "proImg");

        ProductFavoriteRecord pfr = productFavoriteRecordDao.findByProIdAndCustomId(proId, custom.getCustomId());
        if(pfr==null) {
            pfr = new ProductFavoriteRecord();
            pfr.setCreateDay(NormalTools.curDate());
            pfr.setCreateTime(NormalTools.curDatetime());
            pfr.setCreateLong(System.currentTimeMillis());
            pfr.setNickname(custom.getNickname());
            pfr.setOpenid(custom.getOpenid());
            pfr.setProId(proId);
            pfr.setProImg(proImg);
            pfr.setProTitle(proTitle);
            pfr.setUnionid(custom.getUnionid());
            pfr.setCustomId(custom.getCustomId());
            productFavoriteRecordDao.save(pfr);
            plusCount(1, proId);
            return JsonResult.success("收藏成功").set("action", "save");
        } else {
            productFavoriteRecordDao.delete(pfr);
            plusCount(-1, proId);
            return JsonResult.success("取消收藏成功").set("action", "delete");
        }
    }

    public JsonResult delete(String params) {
        Integer id = JsonTools.getId(params);
        WxCustomDto custom = JsonTools.getCustom(params);
        productFavoriteRecordDao.deleteByIdAndCustomId(id, custom.getCustomId());
        return JsonResult.success("删除收藏成功");
    }

    private void plusCount(Integer amount, Integer proId) {
        rabbitNormalTools.updateData("productDao", "plusFavoriteCount", amount, proId);
    }
}
