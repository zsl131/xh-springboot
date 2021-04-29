package com.zslin.business.finance.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.finance.dao.IFinanceDetailDao;
import com.zslin.business.finance.dao.IFinanceRecordDao;
import com.zslin.business.finance.dao.IFinanceTicketDao;
import com.zslin.business.finance.dto.NoDto;
import com.zslin.business.finance.model.FinanceDetail;
import com.zslin.business.finance.model.FinanceRecord;
import com.zslin.business.finance.model.FinanceTicket;
import com.zslin.business.finance.tools.MoneyTools;
import com.zslin.business.finance.tools.TicketNoTools;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dao.IAdminUserDao;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.model.AdminUser;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsl on 2019/1/10.
 */
@Service
@AdminAuth(name = "账务登记", psn = "财务管理", url = "/admin/financeRecord", type = "1", orderNum = 1)
@HasTemplateMessage
public class FinanceRecordService {

    @Autowired
    private IFinanceRecordDao financeRecordDao;

    @Autowired
    private IFinanceDetailDao financeDetailDao;

    @Autowired
    private IFinanceTicketDao financeTicketDao;

    @Autowired
    private IAdminUserDao userDao;

    @Autowired
    private TicketNoTools ticketNoTools;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public JsonResult list(String params) {
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<FinanceRecord> res = financeRecordDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));
        Float totalIn = financeRecordDao.sum("1");
        Float totalOut = financeRecordDao.sum("-1");
        return JsonResult.success().set("size", (int)res.getTotalElements()).set("data", res.getContent())
                .set("totalIn", totalIn).set("totalOut", totalOut);
    }

    public JsonResult loadOne(String params) {
        Integer id = JsonTools.getId(params);
        FinanceRecord record = financeRecordDao.findOne(id);
        List<FinanceDetail> detailList = financeDetailDao.findByTicketNo(record.getTicketNo());
        List<FinanceTicket> ticketList = financeTicketDao.findByTicketNo(record.getTicketNo());
        return JsonResult.success().set("record", record).set("detailList", detailList)
                .set("ticketList", ticketList)
                .set("chineseMoney", MoneyTools.digitUppercase(record.getAmount()));
    }

    @AdminAuth(name = "账目审核", orderNum = 2)
    @TemplateMessageAnnotation(name = "对账单生成通知", keys = "对账单号-账单金额")
    public JsonResult updateStatus(String params) {
        String status = JsonTools.getJsonParam(params, "status");
        Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
        String reason = JsonTools.getJsonParam(params, "reason");
        String name = "", phone = "";
        try {
            String username = getUsername(params);
            AdminUser u = userDao.findByUsername(username);
            name = u.getUsername()+"-"+u.getNickname();
            phone = u.getPhone();
        } catch (Exception e) {
        }
        FinanceRecord fr = financeRecordDao.findOne(id);
        String verifyTime = NormalTools.curDatetime();
        if("1".equals(status)) {
            financeRecordDao.updateStatusByPass("1", name, verifyTime, id);
            //审核通过后发送财务通知
            sendTemplateMessageTools.send2Wx(buildFinanceOpenids(), "对账单生成通知", "", "有财务账单需要处理",
                    TemplateMessageTools.field("对账单号", fr.getTicketNo()),
                    TemplateMessageTools.field("账单金额", fr.getAmount()+" 元"),
                    TemplateMessageTools.field("消费笔数："+fr.getDetailCount() + " 笔"));

            /*SendMessageDto smd = new SendMessageDto("对账单生成通知", buildFinanceOpenids(), "#", "有财务账单需要处理",
                    TemplateMessageTools.field("对账单号", fr.getTicketNo()),
                    TemplateMessageTools.field("账单金额", fr.getAmount()+" 元"),
                    TemplateMessageTools.field("消费笔数："+fr.getDetailCount() + " 笔"));
            rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.DIRECT_ROUTING, smd);*/
        } else {
            financeRecordDao.updateStatusByInvalid("-1", reason, name, phone, verifyTime, id);
        }
        financeDetailDao.updateStatus(status, reason, name, phone, fr.getTicketNo());
        return JsonResult.success("操作成功");
    }

    private List<String> buildFinanceOpenids() {
        List<String> res = new ArrayList<>();
        res.add("oy8_QwNcCgN4U8ulmskM6XeW3YWU"); //钟述林
        res.add("oy8_QwBHRRggNYBZqNqQgCQ_mWjc"); //曾淼
        return res;
    }

    private List<String> buildVerifyOpenids() {
        List<String> res = new ArrayList<>();
        res.add("oy8_QwNcCgN4U8ulmskM6XeW3YWU"); //钟述林
        res.add("oy8_QwBHRRggNYBZqNqQgCQ_mWjc"); //曾淼
        return res;
    }

    @AdminAuth(name = "账目审核", orderNum = 2)
    @TemplateMessageAnnotation(name = "对账单生成通知", keys = "对账单号-账单金额")
    public JsonResult save(String params) {
        String flag = JsonTools.getJsonParam(params, "flag");
        String details = JsonTools.getJsonParam(params, "details");
        JSONArray detailArray = JsonTools.str2JsonArray(details);
        String createDate = NormalTools.curDate();
        String createTime = NormalTools.curDatetime();
        Long createLong = System.currentTimeMillis();
        String createMonth = createDate.replaceAll("-", "").substring(0, 6);
        NoDto noDto = ticketNoTools.getNewRecordTicketNo(createMonth);
        AdminUser u = null;
        try {
            String username = getUsername(params);
            u = userDao.findByUsername(username);
        } catch (Exception e) {
        }
        Float totalAmount = 0f;
        Integer totalCount = detailArray.size();
        Integer ticketCount = 0;
        for(int i=0;i<detailArray.size();i++) {
            JSONObject jsonObj = detailArray.getJSONObject(i);
            JSONArray picArray = jsonObj.getJSONArray("tickets"); //图片
            Integer cateId = jsonObj.getInteger("cateId"); //分类ID
            String cateName = jsonObj.getString("cateName");
            Float price = Float.parseFloat(jsonObj.get("price").toString());
            Integer count = jsonObj.getInteger("count");
            String recordDate = jsonObj.getString("recordDate").replaceAll("-", "");
            String title = jsonObj.getString("title");
            String handleName = jsonObj.getString("handleName"); //经办人
            FinanceDetail fd = new FinanceDetail();
            fd.setRecordDate(recordDate);
            fd.setTicketCount(picArray.size());
            fd.setAmount(price * count);
            fd.setCateName(cateName);
            fd.setCateId(cateId);
            fd.setCount(count);
            fd.setFlag(flag);
            fd.setHandleName(handleName);
            fd.setPrice(price);
            fd.setRecordMonth(recordDate.substring(0, 6));
            fd.setRecordYear(recordDate.substring(0, 4));
            fd.setTitle(title);
            fd.setCreateDate(createDate);
            fd.setCreateTime(createTime);
            fd.setCreateLong(createLong);
            fd.setTicketNo(noDto.getNo());
            fd.setStatus("0");
            fd.setTno(noDto.getTno());
            if(u!=null) {
                fd.setRecordName(u.getUsername()+"-"+u.getNickname());
                fd.setOperator(u.getUsername()+"-"+u.getNickname());
                fd.setRecordPhone(u.getPhone());
            }
            ticketCount += fd.getTicketCount();
            financeDetailDao.save(fd);
            totalAmount += fd.getAmount(); //合计金额
            for(int j=0;j<picArray.size();j++) {
                FinanceTicket ft = new FinanceTicket();
                ft.setDetailId(fd.getId());
                ft.setTicketNo(noDto.getNo());
                ft.setPicUrl(picArray.get(j).toString());
                financeTicketDao.save(ft);
            }
        }
        FinanceRecord fr = new FinanceRecord();
        fr.setTicketNo(noDto.getNo());
        fr.setTno(noDto.getTno());
        if(u!=null) {
            fr.setRecordName(u.getUsername()+"-"+u.getNickname());
            fr.setOperator(u.getUsername()+"-"+u.getNickname());
            fr.setRecordPhone(u.getPhone());
        }
        fr.setFlag(flag);
        fr.setAmount(totalAmount);
        fr.setDetailCount(totalCount);
        fr.setCreateDate(createDate);
        fr.setCreateTime(createTime);
        fr.setCreateLong(createLong);
        fr.setRecordMonth(createMonth);
        fr.setRecordDate(createDate.replaceAll("-", ""));
        fr.setRecordYear(createDate.substring(0, 4));
        fr.setTicketCount(ticketCount);
        fr.setStatus("0"); //待审核
        financeRecordDao.save(fr);

        //添加时发送财务通知请求审核
        /*templateMessageTools.sendMessageByThread("对账单通知", buildVerifyOpenids(), "#", "有财务账单需要审核",
                TemplateMessageTools.field("对账单号", fr.getTicketNo()),
                TemplateMessageTools.field("帐单名称", fr.getRecordName()),
                TemplateMessageTools.field("消费笔数", fr.getDetailCount() + " 笔"),
                TemplateMessageTools.field("消费金额", fr.getAmount()+" 元"),
                TemplateMessageTools.field("生成时间", fr.getCreateTime()),
                TemplateMessageTools.field("请尽快登陆后台审核"));*/

        sendTemplateMessageTools.send2Wx(buildVerifyOpenids(), "对账单生成通知", "", "有财务账单需要审核",
                TemplateMessageTools.field("对账单号", fr.getTicketNo()),
                TemplateMessageTools.field("账单金额", fr.getAmount()+" 元"),
                TemplateMessageTools.field("消费笔数："+fr.getDetailCount() + " 笔\\n新账单需处理"));

        return JsonResult.success("保存成功");
    }

    private String getUsername(String params) {
        try {
            String headerParams = JsonTools.getJsonParam(params, "headerParams");
            String username = JsonTools.getJsonParam(headerParams, "username");
            return username;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
