package com.zslin.business.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.dao.IAgentApplyVerifyDao;
import com.zslin.business.dao.IAgentDao;
import com.zslin.business.dao.IAgentLevelDao;
import com.zslin.business.dao.IAgentPaperDao;
import com.zslin.business.model.Agent;
import com.zslin.business.model.AgentApplyVerify;
import com.zslin.business.model.AgentPaper;
import com.zslin.business.tools.AgentTools;
import com.zslin.business.tools.MediumTools;
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
import com.zslin.core.exception.BusinessException;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.repository.SpecificationOperator;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@HasTemplateMessage
public class MiniAgentService {

    @Autowired
    private IAgentDao agentDao;

    @Autowired
    private IAgentPaperDao agentPaperDao;

    @Autowired
    private IAgentLevelDao agentLevelDao;

    @Autowired
    private IAgentApplyVerifyDao agentApplyVerifyDao;

    @Autowired
    private MediumTools mediumTools;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @Autowired
    private AgentTools agentTools;

    /**
     * ??????????????????
     * @param params
     * @return
     */
    public JsonResult listSub(String params) {
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        WxCustomDto customDto = JsonTools.getCustom(params);
        Page<Agent> res = agentDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                new SpecificationOperator("leaderOpenid", "eq", customDto.getOpenid()),
                new SpecificationOperator("status", "eq", "1")),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("data", res.getContent());
    }

    /**
     * ???????????????
     * @param params
     * @return
     */
    public JsonResult buildCode(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String code = agentTools.buildOwnCode(customDto.getCustomId());
        return JsonResult.success().set("code", code);
    }

    /**
     * ???????????????
     * @param params
     * @return
     */
    public JsonResult bindCode(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String code = JsonTools.getJsonParam(params, "code");
        Agent leader = agentDao.findByOwnCode(code);
        if(leader==null) {
            return JsonResult.success("??????????????????").set("flag", "0");
        }
        Agent agent = agentDao.findByCustomId(customDto.getCustomId());
        if(agent.getLeaderCode()!=null && !"".equals(agent.getLeaderCode().trim())) {
            String name = (leader.getName()==null||"".equals(leader.getName()))?leader.getNickname():leader.getName();
            return JsonResult.success("??????????????????"+name+"???").set("flag", "0");
        }
        agent.setLeaderId(leader.getId());
        agent.setLeaderName(leader.getName());
        agent.setLeaderPhone(leader.getPhone());
        agent.setLeaderOpenid(leader.getOpenid());
        agent.setLeaderCode(leader.getOwnCode());
        agent.setLeaderDate(NormalTools.curDate());
        agent.setLeaderTime(NormalTools.curDatetime());
        agent.setLeaderLong(System.currentTimeMillis());
        agent.setName((agent.getName()==null||"".equals(agent.getName()))?agent.getNickname():agent.getName());
        agent.setStatus("1");
        agentDao.save(agent);
        return JsonResult.success("????????????").set("flag", "1");
    }

    /**
     * ??????????????????????????????
     * @param params
     * @return
     */
    @TemplateMessageAnnotation(name = "??????????????????", keys = "?????????-????????????")
    public JsonResult updateAgent(String params) {
        //System.out.println("=======>"+params);
        WxCustomDto dto = JsonTools.getCustom(params);
        //System.out.println(dto);

        Integer id = JsonTools.getId(params);

        //System.out.println("+++++++++ID::: "+id);
        Agent agent = agentDao.findOne(id); //????????????
        if(!"2".equals(agent.getStatus())) {throw new BusinessException(BusinessException.Code.STATUS_ERROR, "????????????????????????");}
        if(!dto.getOpenid().equals(agent.getOpenid())) {throw new BusinessException(BusinessException.Code.AUTH_ERROR, "???????????????");}

        Agent o = JSONObject.toJavaObject(JSON.parseObject(params), Agent.class);

        agent.setName(o.getName());
        agent.setCityCode(o.getCityCode());
        agent.setCityName(o.getCityName());
        agent.setCountyCode(o.getCountyCode());
        agent.setCountyName(o.getCountyName());
        agent.setHasExperience(o.getHasExperience());
        agent.setIdentity(o.getIdentity());
        agent.setPhone(o.getPhone());
        agent.setProvinceCode(o.getProvinceCode());
        agent.setProvinceName(o.getProvinceName());
        agent.setSex(o.getSex());
        agent.setStreet(o.getStreet());
        agent.setAddressIndex(o.getAddressIndex());
        agent.setCustomId(dto.getCustomId());
        agent.setStatus("0"); //?????????????????????????????????????????????
        if(o.getLeaderId()!=null && o.getLeaderId()>0) { //?????????????????????
            agent.setLeaderId(o.getLeaderId());
            agent.setLeaderName(o.getLeaderName());
            agent.setLeaderPhone(o.getLeaderPhone());
            agent.setLeaderOpenid(o.getLeaderOpenid());
        }
        agentDao.save(agent);

        JSONArray jsonArray = JsonTools.str2JsonArray(JsonTools.getJsonParam(params, "papers")); //????????????
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            AgentPaper ap = new AgentPaper();
            ap.setAgentId(o.getId());
            ap.setAgentName(o.getName());
            ap.setFileName(jsonObj.getString("name"));
            ap.setFilePath(jsonObj.getString("url"));
            ap.setMediumId(jsonObj.getInteger("id"));

            deleteOldPaper(ap.getAgentId(), ap.getFileName()); //??????????????????
//            System.out.println("----->" + jsonObj.toJSONString());
            agentPaperDao.save(ap);
        }

        sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "??????????????????", "", agent.getName()+"???????????????????????????",
                TemplateMessageTools.field("?????????", agent.getName()),
                TemplateMessageTools.field("????????????", agent.getName()+"-"+agent.getPhone()),
                TemplateMessageTools.field("?????????????????????????????????"));

        return JsonResult.success("???????????????????????????");
    }

    private void deleteOldPaper(Integer agentId, String fileName) {
        AgentPaper oldPaper = agentPaperDao.findByAgentIdAndFileName(agentId, fileName);
        if(oldPaper!=null) {
            mediumTools.deleteMedium(oldPaper.getMediumId()); //??????????????????
            agentPaperDao.delete(oldPaper);
        }
    }

    /**
     * ????????????
     * @param params
     * @return
     */
    @NeedAuth(openid = true)
    @Transactional
    @TemplateMessageAnnotation(name = "??????????????????", keys = "?????????-????????????")
    public JsonResult addAgent(String params) {
        //System.out.println("---->"+params);
        WxCustomDto dto = JsonTools.getCustom(params);
        //System.out.println(dto);

        Agent old = agentDao.findByOpenid(dto.getOpenid());
        if(old!=null) {
            throw new BusinessException(BusinessException.Code.HAS_EXISTS, "??????????????????????????????????????????");
        }
//        System.out.println(dto);
        Agent o = JSONObject.toJavaObject(JSON.parseObject(params), Agent.class);
        o.setOpenid(dto.getOpenid());
        o.setUnionid(dto.getUnionid());
        o.setNickname(dto.getNickname());
        o.setCustomId(dto.getCustomId());
        agentDao.save(o);

        JSONArray jsonArray = JsonTools.str2JsonArray(JsonTools.getJsonParam(params, "papers")); //????????????
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            AgentPaper ap = new AgentPaper();
            ap.setAgentId(o.getId());
            ap.setAgentName(o.getName());
            ap.setFileName(jsonObj.getString("name"));
            ap.setFilePath(jsonObj.getString("url"));
            ap.setMediumId(jsonObj.getInteger("id"));
//            System.out.println("----->" + jsonObj.toJSONString());
            agentPaperDao.save(ap);
        }

        agentDao.updatePaperCount(jsonArray.size(), o.getId());

        sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "??????????????????", "", o.getName()+"?????????????????????",
                TemplateMessageTools.field("?????????", o.getName()),
                TemplateMessageTools.field("????????????", o.getName()+"-"+o.getPhone()),
                TemplateMessageTools.field("?????????????????????????????????"));

        return JsonResult.success("????????????");
    }

    /**
     * ????????????
     * @param params
     * @return
     */
    @NeedAuth(openid = true)
    public JsonResult loadOne(String params) {
        WxCustomDto dto = JsonTools.getCustom(params);
        Agent agent = agentDao.findByOpenid(dto.getOpenid());
        JsonResult result = JsonResult.getInstance();
        if(agent!=null) {
            List<AgentApplyVerify> verifyList = agentApplyVerifyDao.findByOpenid(dto.getOpenid(), SimpleSortBuilder.generateSort("id_d"));
            List<AgentPaper> paperList = agentPaperDao.findByAgentId(agent.getId());
            result.set("verifyList", verifyList).set("paperList", paperList);

            /*if("1".equals(agent.getStatus())) { //?????????????????????
                List<AgentLevel> levelList = agentLevelDao.findAll();
                result.set("levelList", levelList);
            }*/
        }
        return result.set("obj", agent);
    }
}
