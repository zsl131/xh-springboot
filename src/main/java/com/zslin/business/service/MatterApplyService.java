package com.zslin.business.service;

import com.zslin.business.dao.IMatterApplyDao;
import com.zslin.business.model.MatterApply;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Created by 钟述林 on 2020-05-11.
 */
@Service
@AdminAuth(name = "物料申请管理", psn = "销售管理", orderNum = 2, type = "1", url = "/admin/matterApply")
@Explain(name = "物料申请管理", notes = "物料申请管理")
public class MatterApplyService {

    @Autowired
    private IMatterApplyDao matterApplyDao;

    @AdminAuth(name = "物料申请列表", orderNum = 1)
    @ExplainOperation(name = "物料申请列表", notes = "物料申请列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "物料申请数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "物料申请数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<MatterApply> res = matterApplyDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "获取物料申请", orderNum = 5)
    @ExplainOperation(name = "获取物料申请信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "物料申请ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            MatterApply obj = matterApplyDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    public JsonResult modifyStatus(String params) {
        try {
            Integer id = JsonTools.getId(params);
            String status = JsonTools.getJsonParam(params, "status");
            matterApplyDao.updateStatus(status, id);
            return JsonResult.success("操作成功");
        } catch (Exception e) {
            return JsonResult.error(e.getMessage());
        }
    }
}
