package com.zslin.business.service;

import com.zslin.business.dao.ISearchRecordDao;
import com.zslin.business.model.SearchRecord;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Created by 钟述林 on 2019-12-18.
 */
@Service
@AdminAuth(name = "搜索记录管理", psn = "移动端管理", orderNum = 2, type = "1", url = "/admin/searchRecord")
@Explain(name = "搜索记录管理", notes = "搜索记录管理")
public class SearchRecordService {

    @Autowired
    private ISearchRecordDao searchRecordDao;

    @AdminAuth(name = "搜索记录列表", orderNum = 1)
    @ExplainOperation(name = "搜索记录列表", notes = "搜索记录列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "搜索记录数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "搜索记录数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<SearchRecord> res = searchRecordDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }
}
