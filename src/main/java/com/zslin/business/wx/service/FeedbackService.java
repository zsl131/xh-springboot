package com.zslin.business.wx.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.business.wx.dao.IFeedbackDao;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.business.wx.model.Feedback;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import com.zslin.core.validate.ValidationDto;
import com.zslin.core.validate.ValidationTools;
import com.zslin.core.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.zslin.core.tools.MyBeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2020-04-14.
 */
@Service
@AdminAuth(name = "微信反馈管理", psn = "微信管理", orderNum = 2, type = "1", url = "/admin/feedback")
@Explain(name = "微信反馈管理", notes = "微信反馈管理")
@HasTemplateMessage
public class FeedbackService {

    @Autowired
    private IFeedbackDao feedbackDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @AdminAuth(name = "微信反馈列表", orderNum = 1)
    @ExplainOperation(name = "微信反馈列表", notes = "微信反馈列表", params= {
             @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
             @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
             @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
             @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
     }, back = {
             @ExplainReturn(field = "size", type = "int", notes = "微信反馈数量"),
             @ExplainReturn(field = "datas", type = "Object", notes = "微信反馈数组对象")
     })
     public JsonResult list(String params) {
         QueryListDto qld = QueryTools.buildQueryListDto(params);
         Page<Feedback> res = feedbackDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                 SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

         return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
     }

    @AdminAuth(name = "修改微信反馈", orderNum = 3)
    @ExplainOperation(name = "修改微信反馈", notes = "修改微信反馈信息", params = {
            @ExplainParam(value = "id", name = "微信反馈id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "对应的对象信息")
    })
    @Transactional
    public JsonResult update(String params) {
        try {
            Feedback o = JSONObject.toJavaObject(JSON.parseObject(params), Feedback.class);
            ValidationDto vd = ValidationTools.buildValidate(o);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            Feedback obj = feedbackDao.findOne(o.getId());
            MyBeanUtils.copyProperties(o, obj, "id", "createDate", "createTime", "createLong", "createDay");
            feedbackDao.save(obj);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @AdminAuth(name = "获取微信反馈", orderNum = 5)
    @ExplainOperation(name = "获取微信反馈信息", notes = "通过ID获取角色对象", params = {
            @ExplainParam(value = "id", name = "微信反馈ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            Feedback obj = feedbackDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "删除微信反馈", orderNum = 4)
    @ExplainOperation(name = "删除微信反馈", notes = "通过ID删除对象", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "删除标识")
    })
    @Transactional
    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            Feedback r = feedbackDao.findOne(id);
            feedbackDao.delete(r);
            return JsonResult.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    public JsonResult updateStatus(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            String status = JsonTools.getJsonParam(params, "status");
            feedbackDao.updateStatus(id, status);
            return JsonResult.success("设置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    /**
     * 回复
     * @param params {id:1, reply: 回复}
     * @return
     */
    @TemplateMessageAnnotation(name = "问题反馈结果通知", keys = "提问内容-回复内容")
    public JsonResult reply(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            String reply = JsonTools.getJsonParam(params, "reply");
            Feedback f = feedbackDao.findOne(id);
            f.setReply(reply);
            f.setReplyDay(NormalTools.curDate());
            f.setReplyTime(NormalTools.curDatetime());
            f.setReplyLong(System.currentTimeMillis());
            feedbackDao.save(f);

            //TODO 通知反馈者
//            boolean result = eventToolsThread.eventRemind(f.getOpenid(), "回复反馈啦", "反馈回复", NormalTools.curDate(), reply, "#");
//            templateMessageTools.sendMessageByThread("反馈回复", f.getOpenid(), "#", "您的反馈信息已得到回复", TemplateMessageTools.field("反馈日期", f.getCreateDate()), TemplateMessageTools.field("反馈内容", f.getContent()), TemplateMessageTools.field("回复内容", reply));

            sendTemplateMessageTools.send2Wx(f.getOpenid(), "问题反馈结果通知", "", "您的消息得到回复",
                    TemplateMessageTools.field("提问内容", f.getContent()),
                    TemplateMessageTools.field("回复内容", reply),
                    TemplateMessageTools.field("请知晓"));

            return JsonResult.succ(f);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

}
