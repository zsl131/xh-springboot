package com.zslin.business.service;

import com.zslin.business.dao.IOrdersDao;
import com.zslin.business.dao.IRefundRecordDao;
import com.zslin.business.mini.tools.PayTools;
import com.zslin.business.model.Orders;
import com.zslin.business.model.RefundRecord;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.LoginUserDto;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Created by 钟述林 on 2020-07-06.
 */
@Service
@AdminAuth(name = "退款记录管理", psn = "销售管理", orderNum = 2, type = "1", url = "/admin/refundRecord")
@Explain(name = "退款记录管理", notes = "退款记录管理")
@HasTemplateMessage
public class RefundRecordService {

    @Autowired
    private IRefundRecordDao refundRecordDao;

    @Autowired
    private IOrdersDao ordersDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @Autowired
    private PayTools payTools;

    @AdminAuth(name = "退款记录列表", orderNum = 1)
    @ExplainOperation(name = "退款记录列表", notes = "退款记录列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "退款记录数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "退款记录数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<RefundRecord> res = refundRecordDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "获取退款记录", orderNum = 5)
    @ExplainOperation(name = "获取退款记录信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "退款记录ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            RefundRecord obj = refundRecordDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @TemplateMessageAnnotation(name = "退款审核结果通知", keys = "申请时间-原订单编号-退款金额-审核结果")
    public JsonResult verify(String params) {
        System.out.println(params);
        LoginUserDto userDto = JsonTools.getUser(params);
        Integer id = JsonTools.getId(params);
        String flag = JsonTools.getJsonParam(params, "flag");
        String reason = JsonTools.getJsonParam(params, "reason");
        RefundRecord rr = refundRecordDao.findOne(id);
        rr.setVerifyFlag(flag);
        rr.setVerfiyReason(reason);
        Orders orders = ordersDao.findOne(rr.getOrdersId());
        orders.setRefundVerifyReason(reason);
        if("1".equals(flag)) { //通过
            orders.setRefundFlag("2");
            //处理退款信息
            payTools.refund(rr.getOrdersProId(), rr.getBackMoney(), reason, userDto, false);
        } else { //驳回
            rr.setVerifyFlag("2"); //驳回
            orders.setRefundFlag("-1");

            //TODO 驳回时通知
            sendTemplateMessageTools.send(orders.getOpenid(), "退款审核结果通知", "", "你的退款申请已【"+("1".equals(flag)?"通过":"驳回")+"】",
                    TemplateMessageTools.field("申请时间", rr.getCreateTime()),
                    TemplateMessageTools.field("原订单编号", orders.getOrdersNo()),
                    TemplateMessageTools.field("退款金额", rr.getBackMoney()+" 元"),
                    TemplateMessageTools.field("审核结果","1".equals(flag)?"通过":"驳回"),

                    TemplateMessageTools.field("审核原因："+reason));
        }
        refundRecordDao.save(rr);
        ordersDao.save(orders);

        return JsonResult.success("操作成功");
    }
}
