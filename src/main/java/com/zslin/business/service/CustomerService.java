package com.zslin.business.service;

import com.zslin.business.dao.IAgentDao;
import com.zslin.business.dao.ICustomerDao;
import com.zslin.business.model.Customer;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Created by 钟述林 on 2019-12-13.
 */
@Service
@AdminAuth(name = "客户管理", psn = "销售管理", orderNum = 2, type = "1", url = "/admin/customer")
@Explain(name = "客户管理", notes = "客户管理")
public class CustomerService {

    @Autowired
    private ICustomerDao customerDao;

    @Autowired
    private IAgentDao agentDao;

    @AdminAuth(name = "客户列表", orderNum = 1)
    @ExplainOperation(name = "客户列表", notes = "客户列表", params = {
            @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
            @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
            @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
            @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "客户数量"),
            @ExplainReturn(field = "datas", type = "Object", notes = "客户数组对象")
    })
    public JsonResult list(String params) {
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<Customer> res = customerDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
    }

    @AdminAuth(name = "获取客户", orderNum = 5)
    @ExplainOperation(name = "获取客户信息", notes = "通过ID获取客户信息", params = {
            @ExplainParam(value = "id", name = "客户ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
//            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            Integer id = JsonTools.getId(params);
            Customer obj = customerDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    /**
     * 绑定手机号码
     */
    @NeedAuth(openid = true)
    public JsonResult bindPhone(String params) {
        try {
            String phone = JsonTools.getJsonParam(params, "phone");
            WxCustomDto customDto = JsonTools.getCustom(params);

            agentDao.updatePhone(phone, customDto.getCustomId()); //修改代理手机号码
            customerDao.updatePhone(phone, customDto.getCustomId()); //修改客户手机号码
            return JsonResult.success("绑定成功");
        } catch (Exception e) {
            return JsonResult.error(e.getMessage());
        }
    }
}
