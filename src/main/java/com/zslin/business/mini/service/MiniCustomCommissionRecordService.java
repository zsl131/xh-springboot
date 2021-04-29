package com.zslin.business.mini.service;

import com.zslin.business.dao.IAgentDao;
import com.zslin.business.dao.ICashOutDao;
import com.zslin.business.dao.ICustomCommissionRecordDao;
import com.zslin.business.mini.dto.AgentCommissionDto;
import com.zslin.business.mini.tools.MiniOrdersTools;
import com.zslin.business.model.Agent;
import com.zslin.business.model.CashOut;
import com.zslin.business.model.CustomCommissionRecord;
import com.zslin.business.tools.AgentTools;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.repository.SpecificationOperator;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import com.zslin.core.tools.RandomTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 小程序处理提成记录
 */
@Service
@HasTemplateMessage
public class MiniCustomCommissionRecordService {

    @Autowired
    private ICustomCommissionRecordDao customCommissionRecordDao;

    @Autowired
    private MiniOrdersTools miniOrdersTools;

    @Autowired
    private IAgentDao agentDao;

    @Autowired
    private ICashOutDao cashOutDao;

    @Autowired
    private AgentTools agentTools;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @NeedAuth(openid = true)
    public JsonResult listOwn(String params) {
        //System.out.println("----MiniCustomCommissionRecordService.listOwn----"+params);
        WxCustomDto customDto = JsonTools.getCustom(params);

        Integer sharedId = JsonTools.getParamInteger(params, "shareId");

        //初始化代理，有则返回，无则新增
        Agent agent = agentTools.initAgent(customDto, sharedId);

//        Integer agentId = agentDao.queryAgentId(customDto.getCustomId());
        Integer agentId = agent.getId();
        List<AgentCommissionDto> dtoList = miniOrdersTools.buildAgentCommission(agentId);
        return JsonResult.success().set("commissionList", dtoList).set("agent", agent);
    }

    /**
     * 获取明细
     */
    @NeedAuth
    public JsonResult list(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String status = JsonTools.getJsonParam(params, "status");

        Integer agentId = agentDao.queryAgentId(customDto.getCustomId()); //先获取代理ID

        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<CustomCommissionRecord> res = customCommissionRecordDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                new SpecificationOperator("agentId", "eq", agentId, "and"), //代理
                new SpecificationOperator("money", "gt", 0, "and"), //金额要大于0
                (status != null && !"".equals(status)) ? new SpecificationOperator("status", "eq", status, "and") : null), //对应状态
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.getInstance().set("size", (int) res.getTotalElements())
                .set("data", res.getContent());
    }

    /**
     * 当用户发起提现
     */
    @NeedAuth(openid = true)
    @TemplateMessageAnnotation(name = "提现申请通知", keys = "申请人-创建时间-申请金额")
    public JsonResult onCashOut(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String batchNo = RandomTools.genTimeNo(3, 5).toUpperCase(); //批次号
        CashOut co = new CashOut();
        Agent agent = agentDao.findByOpenid(customDto.getOpenid());
        if (agent != null && cashOutDao.findByRunningByAgentId(agent.getId()) == null) { //不能有在提现中的数据

            AgentCommissionDto dto = customCommissionRecordDao.queryCountDtoNoBatchNo("2", agent.getId());

            String createDay = NormalTools.curDate();
            String createTime = NormalTools.curDatetime();
            Long createLong = System.currentTimeMillis();

            co.setAgentId(agent.getId());
            co.setAgentName(agent.getName());
            co.setAgentOpenid(agent.getOpenid());
            co.setAgentPhone(agent.getPhone());
            co.setBatchNo(batchNo);
            co.setCreateDay(createDay);
            co.setCreateTime(createTime);
            co.setCreateLong(createLong);
            co.setAmount((int) dto.getTotalCount());
            co.setMoney((float) dto.getMoney());
            co.setStatus("0");

            cashOutDao.save(co); //保存记录

            customCommissionRecordDao.updateByHql("UPDATE CustomCommissionRecord c SET c.cashOutBatchNo=?1, c.status=?2," +
                    " c.cashOutDay=?3, c.cashOutTime=?4, c.cashOutLong=?5 WHERE " +
                    " c.status=?6 AND c.agentId=?7 AND c.cashOutBatchNo IS NULL ",
                    batchNo, "3", createDay, createTime, createLong, "2", agent.getId());
//            customCommissionRecordDao.updateBatchNo(batchNo, "3", createDay, createTime, createLong,
//                    "2", agent.getId()); //当发起提现时的操作

            //把之前处理遗漏的数据重新更新一下，将状态修改为3-纳入结算清单
//            customCommissionRecordDao.update2CashOut("3");

            String name = (agent.getName() == null || "".equals(agent.getName())) ? agent.getNickname() : agent.getName();
            //申请人-创建时间-申请金额
            sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "提现申请通知", "", name + " 发起了提现申请",
                    TemplateMessageTools.field("申请人", name),
                    TemplateMessageTools.field("创建时间", NormalTools.curDatetime()),
                    TemplateMessageTools.field("申请金额", co.getMoney() + ""),

                    TemplateMessageTools.field(agent.getPhone()));

            return JsonResult.success("提现申请成功，等待审核").set("flag", "1");
        } else {
            return JsonResult.success("提现失败，存在未完成的提现业务").set("flag", "0");
        }
    }
}
