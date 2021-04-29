package com.zslin.core.service;


import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.SortTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminPublicService {

    @Autowired
    private SortTools sortTools;

    @ExplainOperation(name = "拖动排序", notes = "拖动排序", params = {
            @ExplainParam(name = "type", value = "排序类型，必须是类名", example = "AdminMenu"),
            @ExplainParam(name = "data", value = "需要修改的JSON数组")
    }, back = {
            @ExplainReturn(field = "message", notes = "初始化结果信息")
    })
    public JsonResult changeOrderNo(String params) {
        String type = JsonTools.getJsonParam(params, "type");
        String data = JsonTools.getJsonParam(params, "data");
        sortTools.handler(type, data);

        return JsonResult.success("设置成功");
    }
}
