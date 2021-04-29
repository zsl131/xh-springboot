package com.zslin.business.tools;

import com.zslin.business.dao.*;
import com.zslin.business.model.*;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.LoginUserDto;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.RandomTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("agentTools")
@HasTemplateMessage
public class AgentTools {

    @Autowired
    private IAgentApplyVerifyDao agentApplyVerifyDao;

    @Autowired
    private IAgentLevelRecordDao agentLevelRecordDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @Autowired
    private IAgentDao agentDao;

    @Autowired
    private ICustomerDao customerDao;

    @Autowired
    private IAgentLevelDao agentLevelDao;

    /*private String buildCode(String openid, Integer id) {
        try {
            //每一次生成都不一样，把后面时间取消则每次生成都一样
            String md5 = SecurityUtil.md5(openid, id+"-"+NormalTools.curDatetime());
            String code = md5.substring(0, 6).toUpperCase(); //取前10位作为邀请码,转换成大写
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            return RandomTools.randomString(6).toUpperCase();
        }
    }*/

    /** 生成6分随机数 */
    private String buildCode() {
        return RandomTools.genCodeNew(); //生成6位随机数
    }

    /**
     * 生成自己的邀请码
     * @param customId Customer的ID
     * @return
     */
    public String buildOwnCode(Integer customId) {
        Agent agent = agentDao.findByCustomId(customId);
        if(agent!=null) {
            if(agent.getOwnCode()!=null && !"".equals(agent.getOwnCode().trim())) {
                return agent.getOwnCode();
            } else {
                try {
                    //每一次生成都不一样，把后面时间取消则每次生成都一样
                    /*String md5 = SecurityUtil.md5(agent.getOpenid(), agent.getId()+"-"+NormalTools.curDatetime());
                    String code = md5.substring(0, 6).toUpperCase(); //取前10位作为邀请码,转换成大写*/
//                    String code = buildCode(agent.getOpenid(), agent.getId());
                    String code = buildCode();
//                    System.out.println("--------code:"+code);
                    while (agentDao.findByOwnCode(code)!=null) {code = buildCode();}
                    agent.setOwnCode(code);
                    agentDao.save(agent);
                    return code;
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    /** 在MiniAuthService中初始化用户信息时调用 */
    public Agent initAgent(Customer customer) {
        WxCustomDto customDto = new WxCustomDto();
        customDto.setHeadImgUrl(customer.getHeadImgUrl());
        customDto.setUnionid(customer.getUnionid());
        customDto.setOpenid(customer.getOpenid());
        customDto.setNickname(customer.getNickname());
        customDto.setCustomId(customer.getId());
        return initAgent(customDto, customer.getLeaderId());
    }

    /** 初始化代理信息 */
    @Transactional
    public synchronized Agent initAgent(WxCustomDto customDto, Integer leaderId) {
        try {
            Agent agent = agentDao.findByOpenid(customDto.getOpenid());
            if(agent==null) {
                agent = new Agent();
                if(leaderId!=null && leaderId>0) { //如果分享者ID不为空
                    Customer leader = customerDao.findOne(leaderId);
                    if(leader!=null) { //如果存在上级
                        agent.setLeaderOpenid(leader.getOpenid());
                        agent.setLeaderPhone(leader.getPhone());
                        agent.setLeaderName(leader.getName()==null?leader.getNickname():leader.getName());
                        agent.setLeaderId(leaderId);
                    }
                }
                agent.setStatus("0"); //默认设置为0
                agent.setSubCount(0); //下级人数
                agent.setRelationCount(0); //代理等级调整次数
                agent.setOpenid(customDto.getOpenid());
                agent.setCustomId(customDto.getCustomId());
                agent.setNickname(customDto.getNickname());
                agent.setUnionid(customDto.getUnionid());
                agent.setName(customDto.getNickname());

                AgentLevel al = agentLevelDao.queryMinLevel();
                if(al!=null) {
                    agent.setLevelName(al.getName());
                    agent.setLevelId(al.getId());
                }
                agent.setCreateDay(NormalTools.curDate());
                agent.setCreateTime(NormalTools.curDatetime());
                agent.setCreateLong(System.currentTimeMillis());
                agent.setUpdateDay(NormalTools.curDate());
                agent.setUpdateTime(NormalTools.curDatetime());
                agent.setUpdateLong(System.currentTimeMillis());
                agentDao.save(agent);

                //建立代理与客户之间的关系
                customerDao.updateName("", "", agent.getId(), agent.getOpenid());
                return agent;
            } else {return agent;}
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @TemplateMessageAnnotation(name = "申请审核通知", keys = "申请人-申请内容")
    public void verify(String params, Agent agent, AgentLevel al) {
        Integer id = JsonTools.getId(params);
        String status = JsonTools.getJsonParam(params, "status");
        String reason = JsonTools.getJsonParam(params, "reason");
        Integer level = JsonTools.getParamInteger(params, "level");

        LoginUserDto dto = JsonTools.getUser(params);

        AgentApplyVerify aav = new AgentApplyVerify();
        aav.setAgentId(id);
        if(al!=null) {
            reason += ("; 等级为："+level+"-"+al.getName());
        }
        aav.setContent(reason);
        aav.setOpenid(agent.getOpenid());
        aav.setUnionid(agent.getUnionid());
        aav.setVerifyDay(NormalTools.curDate());
        aav.setVerifyTime(NormalTools.curDatetime());
        aav.setVerifyLong(System.currentTimeMillis());
        aav.setVerifyOperator(dto.getId()+"-"+dto.getUsername()+"-"+dto.getNickname());
        aav.setVerifyRes(status);

        agentApplyVerifyDao.save(aav);

        /*String remark = "恭喜您！";
        if("1".equals(status) && al!=null) {remark = al.getName();}
        else if("2".equals(status)) {remark = reason;}
        rabbitNormalTools.pushMessage("AGENT-VERIFY", agent.getOpenid(), "pages/agent/apply/apply",
                "代理审核", "1".equals(status)?"审核通过":"审核不通过", NormalTools.curDate(),
                remark, agent.getName());*/

        addLevelRecord(agent, al, reason);

        String msgTitle = "很遗憾，审核不通过！";
        if("1".equals(status)) { //只有审核通过才进行等级调整
            msgTitle = "恭喜您，审核通过！";
        }

        sendTemplateMessageTools.send(agent.getOpenid(), "申请审核通知", "", msgTitle,
                TemplateMessageTools.field("申请人", agent.getName()),
                TemplateMessageTools.field("申请内容", "1".equals(status)?"通过":"驳回"),
                TemplateMessageTools.field("1".equals(status)?msgTitle+(al==null?"":"等级为："+al.getName()):"原因："+reason));
    }

    private void addLevelRecord(Agent agent, AgentLevel al, String reason) {
        if(al==null) {return;}
        AgentLevelRecord alr = new AgentLevelRecord();
        alr.setAgentId(agent.getId());
        alr.setBeforeLevelId(agent.getLevelId());
        alr.setBeforeLevelName(agent.getLevelName());
        alr.setCreateDay(NormalTools.curDate());
        alr.setCreateLong(System.currentTimeMillis());
        alr.setCreateTime(NormalTools.curDatetime());
        alr.setCurLevelId(al.getId());
        alr.setCurLevelName(al.getName());
//        alr.setFlag();
        alr.setReason(reason);
        agentLevelRecordDao.save(alr);
    }
}
