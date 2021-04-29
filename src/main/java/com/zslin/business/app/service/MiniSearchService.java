package com.zslin.business.app.service;

import com.zslin.business.dao.IProductDao;
import com.zslin.business.dao.IProductTagDao;
import com.zslin.business.dao.ISearchRecordDao;
import com.zslin.business.model.Product;
import com.zslin.business.model.ProductTag;
import com.zslin.business.model.SearchRecord;
import com.zslin.core.annotations.NeedAuth;
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

import java.util.List;

@Service
public class MiniSearchService {

    @Autowired
    private ISearchRecordDao searchRecordDao;

    @Autowired
    private IProductDao productDao;

    @Autowired
    private RabbitNormalTools rabbitNormalTools;

    @Autowired
    private IProductTagDao productTagDao;

    @NeedAuth(openid = true)
    public JsonResult search(String params) {
        JsonResult result = JsonResult.getInstance();
        String keyword = JsonTools.getJsonParam(params, "keyword");
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        String searchType = JsonTools.getJsonParam(params, "searchType"); //搜索类型，0-产品；1-资讯
        if("0".equals(searchType)) {
            Page<Product> res = productDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                    new SpecificationOperator("status", "eq", "1", "and"),
                    new SpecificationOperator("title", "like", keyword, "and",
                            new SpecificationOperator("content", "like", keyword, "or"))),
                    SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));
            result.set("productList", res.getContent());
        } else { //搜索其他内容
            /*Page<Product> res = productDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                    new SpecificationOperator("status", "eq", "1", "and"),
                    new SpecificationOperator("title", "like", keyword, "and",
                            new SpecificationOperator("content", "like", keyword, "or"))),
                    SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));
            result.set("productList", res.getContent());*/
        }
        rabbitNormalTools.updateData("searchRecordTools", "record", params);
        return result.set("tag", queryTag(keyword));
    }

    private ProductTag queryTag(String keyword) {
        List<ProductTag> list = productTagDao.findByName(keyword);
        if(list!=null && list.size()>0) {return list.get(0);}
        return null;
    }

    @NeedAuth(openid = true)
    public JsonResult cleanAll(String params) {
        try {
            WxCustomDto custom = JsonTools.getCustom(params);
            searchRecordDao.cleanRecord(custom.getCustomId());
            return JsonResult.success("清空完成");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error("操作失败：", e.getMessage());
        }
    }

    @NeedAuth(openid = true)
    public JsonResult onSearch(String params) {
        //默认获取20条数据
        try {
            WxCustomDto custom = JsonTools.getCustom(params);
            Page<ProductTag> tagList = productTagDao.findAll(QueryTools.getInstance().buildSearch(
                    new SpecificationOperator("status", "eq", "1")),
                    SimplePageBuilder.generate(0, 20, SimpleSortBuilder.generateSort("orderNo_a")));

            Page<SearchRecord> ownList = searchRecordDao.findAll(QueryTools.getInstance().buildSearch(
                    new SpecificationOperator("customId", "eq", custom.getCustomId())),
                    SimplePageBuilder.generate(0, 20, SimpleSortBuilder.generateSort("updateLong_d")));

            return JsonResult.success().set("tagList", tagList.getContent()).set("ownList", ownList.getContent());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.success();
        }
    }
}
