package com.zslin.business.tools;

import com.zslin.business.dao.IAgentDao;
import com.zslin.business.dao.IAgentLevelDao;
import com.zslin.business.dao.IAgentRateDefaultDao;
import com.zslin.business.model.AgentLevel;
import com.zslin.business.model.AgentRateDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 代理提成标准工具类
 */
@Component
public class AgentRateTools {

    @Autowired
    private IAgentLevelDao agentLevelDao;

    @Autowired
    private IAgentRateDefaultDao agentRateDefaultDao;

    /**
     * 生成默认提成标准
     */
    public void initDefault() {
        List<AgentLevel> levelList = agentLevelDao.findAll();
        List<AgentRateDefault> rateList = agentRateDefaultDao.findAll();
        for(AgentRateDefault ard : rateList) {
            if(!contains(levelList, ard.getLevelId())) {agentRateDefaultDao.deleteById(ard.getId());} //如果等级中不存在，则删除
        }
        for(AgentLevel level: levelList) {
            AgentRateDefault ard = agentRateDefaultDao.findByLevelId(level.getId());
            if(ard==null) {
                ard = new AgentRateDefault(); ard.setAmount(0f);
            }
            ard.setLevelId(level.getId());
            ard.setLevelName(level.getName());
            agentRateDefaultDao.save(ard);
        }
    }

    private boolean contains(List<AgentLevel> levelList, int levelId) {
        boolean res = false;
        for(AgentLevel level : levelList) {
            if(level.getId()==levelId) {res = true; return res;}
        }
        return res;
    }
}
