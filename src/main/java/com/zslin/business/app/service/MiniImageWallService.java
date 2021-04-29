package com.zslin.business.app.service;

import com.zslin.business.mini.dao.ICustomImageRelationDao;
import com.zslin.business.mini.dao.IImageWallDao;
import com.zslin.business.mini.model.ImageWall;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.repository.SpecificationOperator;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 小程序影像
 */
@Service
public class MiniImageWallService {

    @Autowired
    private IImageWallDao imageWallDao;

    @Autowired
    private ICustomImageRelationDao customImageRelationDao;

    public JsonResult listShow(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<ImageWall> res = imageWallDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                new SpecificationOperator("status", "eq", "1"),
                new SpecificationOperator("customId", "eq", customDto.getCustomId(), "or")),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        String type = customImageRelationDao.findType(customDto.getCustomId()); //关系类型
        List<ImageWall> noFinished = imageWallDao.findNoFinished(customDto.getCustomId(), SimpleSortBuilder.generateSort("id_d"));
        return JsonResult.success().set("imageList", res.getContent()).set("type", type).set("noFinished", noFinished);
    }

    /** 获取对象 */
    public JsonResult loadOne(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        Integer id = JsonTools.getId(params);
        ImageWall image = imageWallDao.findByIdAndCustomId(id, customDto.getCustomId());
        return JsonResult.success().set("image", image);
    }

    /** 发布 */
    public JsonResult modify(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        Integer id = JsonTools.getId(params);
        String title = JsonTools.getJsonParam(params, "title");
        ImageWall image = imageWallDao.findByIdAndCustomId(id, customDto.getCustomId());
        String type = customImageRelationDao.findType(customDto.getCustomId());
        image.setTitle(title);
        if("1".equals(type)) {
            image.setStatus("0");
        } else if("2".equals(type)) {
            image.setStatus("1");
        }
        imageWallDao.save(image);
        return JsonResult.success("发布成功");
    }

    public JsonResult plusGoodCount(String params) {
        try {
            Integer id = JsonTools.getId(params);
            Integer count = JsonTools.getParamInteger(params, "count");
            imageWallDao.plusGoodCount(count, id);
            return JsonResult.success("点赞成功");
        } catch (Exception e) {
            return JsonResult.error(e.getMessage());
        }
    }
}
