package com.zslin.business.mini.service;

import com.zslin.business.dao.ICustomCommissionRecordDao;
import com.zslin.business.settlement.dao.IReceiptRecordDao;
import com.zslin.business.settlement.dao.IRewardDao;
import com.zslin.business.settlement.dao.IRewardRuleDao;
import com.zslin.business.settlement.dto.AgentRewardDto;
import com.zslin.business.settlement.model.ReceiptRecord;
import com.zslin.business.settlement.model.Reward;
import com.zslin.business.settlement.model.RewardRule;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.common.NormalTools;
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
import org.springframework.stereotype.Service;

/**
 * 提成奖金
 */
@Service
@HasTemplateMessage
public class MiniRewardService {

    @Autowired
    private IRewardRuleDao rewardRuleDao;

    @Autowired
    private IRewardDao rewardDao;

    @Autowired
    private IReceiptRecordDao receiptRecordDao;

    @Autowired
    private ICustomCommissionRecordDao customCommissionRecordDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    /** 获取个人提成奖金 */
    public JsonResult query(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        RewardRule rule = rewardRuleDao.loadOne();

        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<Reward> res = rewardDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                new SpecificationOperator("customOpenid", "eq", customDto.getOpenid())),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        //代理奖金DTO对象
        AgentRewardDto dto = rewardDao.queryDto(customDto.getOpenid());

        return JsonResult.success().set("rule", rule).set("rewardList", res.getContent()).set("agentReward", dto);
    }

    /** 获取个人的奖金领取记录 */
    public JsonResult listRecord(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);

        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<ReceiptRecord> res = receiptRecordDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                new SpecificationOperator("customOpenid", "eq", customDto.getOpenid())),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.success().set("recordList", res.getContent()).set("totalSize", res.getTotalElements());
    }

    /** 申请领取奖金 */
    @TemplateMessageAnnotation(name = "提现申请通知", keys = "申请人-创建时间-申请金额")
    public JsonResult applyReceipt(String params) {
        JsonResult result = JsonResult.getInstance();
        String month = JsonTools.getJsonParam(params, "month"); //奖金月份
        WxCustomDto customDto = JsonTools.getCustom(params);
        Reward reward = rewardDao.findByProduceMonthAndCustomOpenid(month, customDto.getOpenid());
        if(reward==null) { //无奖金情况
            result.set("message", month+" 月份未获得任何奖金哦").set("flag", "0");
            return result;
        } else { //有奖金情况
            if("1".equals(reward.getStatus()) || reward.getSurplusMoney()<=0) { //奖金已领取完成情况
                result.set("message", month+" 月份已经领取完成").set("flag", "0");
                return result;
            } else {
                String preMonth = NormalTools.getMonth("yyyyMM", -1); //获取上个月月份
                Long count = customCommissionRecordDao.queryCount(preMonth, customDto.getOpenid()); //获取上个月是否有业绩
                if(count<=0) {
                    result.set("message", preMonth+" 月份没有业绩，不能领取").set("flag", "0");
                    return result;
                }
                String curMonth = NormalTools.getMonth("yyyyMM"); //当前月份

                Long receiptCount = receiptRecordDao.queryCount(month, curMonth, customDto.getOpenid());
                if(receiptCount>0) {
                    result.set("message", curMonth+" 月份已经领取，不能重复领取").set("flag", "0");
                    return result;
                } else {
                    ReceiptRecord rr = new ReceiptRecord();
                    rr.setAgentId(reward.getAgentId());
                    rr.setAgentName(reward.getAgentName());
                    rr.setAgentPhone(reward.getAgentPhone());
                    rr.setCreateDay(NormalTools.curDate());
                    rr.setCreateLong(System.currentTimeMillis());
                    rr.setCreateMonth(curMonth);
                    rr.setCreateTime(NormalTools.curDatetime());
                    rr.setCustomId(reward.getCustomId());
                    rr.setCustomNickname(reward.getCustomNickname());
                    rr.setCustomOpenid(reward.getCustomOpenid());
                    rr.setRewardId(reward.getId());
                    rr.setRewardProduceMonth(reward.getProduceMonth());
                    rr.setRewardProduceYear(reward.getProduceYear());
                    rr.setStatus("0");

                    Float receiptMoney = buildReceiptMoney(reward); //生成应得的金额
                    Integer curTimes = reward.getReceiptTimes() + 1; //当前次数

                    rr.setMoney(receiptMoney);
                    rr.setTimes(curTimes); //TODO

                    receiptRecordDao.save(rr); //保存
                    reward.setReceiptMoney(reward.getReceiptMoney()+receiptMoney); //设置已领取金额
                    reward.setSurplusMoney(reward.getExtraMoney() - reward.getReceiptMoney());
                    reward.setReceiptTimes(curTimes);
                    reward.setSurplusTimes(reward.getTotalTimes() - reward.getReceiptTimes());
                    if(reward.getSurplusTimes()<=0 || reward.getSurplusMoney()<=0) {reward.setStatus("1");} //设置已领取完成
                    rewardDao.save(reward); //修改奖金数据

                    String name = (reward.getAgentName()==null||"".equals(reward.getAgentName()))?reward.getCustomNickname():reward.getAgentName();

                    //申请人-创建时间-申请金额
                    sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "提现申请通知", "", name+" 要领取奖金",
                            TemplateMessageTools.field("申请人", name),
                            TemplateMessageTools.field("创建时间", NormalTools.curDatetime()),
                            TemplateMessageTools.field("申请金额", receiptMoney+""),

                            TemplateMessageTools.field(reward.getAgentPhone()));

                    result.set("message", "成功提交领取申请，等待后台审核，本次领取金额【"+receiptMoney+"】，还可领取【"+reward.getSurplusTimes()+"】次！注意接听电话哦，如果还未绑定手机号码请到个人中心绑定").set("flag", "1");
                    return result;
                }
            }
        }
    }

    ///获取每次
    private Float buildReceiptMoney(Reward r) {
        return r.getExtraMoney() / r.getTotalTimes();
    }
}
