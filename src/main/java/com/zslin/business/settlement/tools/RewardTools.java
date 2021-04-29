package com.zslin.business.settlement.tools;

import com.zslin.business.settlement.dao.IRewardDao;
import com.zslin.business.settlement.dao.IRewardRuleDao;
import com.zslin.business.settlement.dao.ISaleRankingDao;
import com.zslin.business.settlement.model.Reward;
import com.zslin.business.settlement.model.RewardRule;
import com.zslin.business.settlement.model.SaleRanking;
import com.zslin.core.common.NormalTools;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 奖励金处理工具类
 */
@Component("rewardTools")
public class RewardTools {

    @Autowired
    private IRewardDao rewardDao;

    @Autowired
    private IRewardRuleDao rewardRuleDao;

    @Autowired
    private ISaleRankingDao saleRankingDao;

    /**
     * 生成奖励金
     */
    public void buildReward() {
        buildReward(NormalTools.getMonth("yyyyMM", -1)); //生成上一个月的
    }

    public void buildReward(String month) {
        RewardRule rr = rewardRuleDao.loadOne();
        if(rr!=null && "1".equals(rr.getStatus())) { //存在规则信息，并且状态是启用

            Long count = rewardDao.queryCount(month);
            if(count>0) {return ;} //如果已经生成过就不能再重复生成

            String year = month.substring(0, 4); //年份
            int page = 0, size = 200, len = size;
            Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
            while(len>=size) {
                Pageable pager = SimplePageBuilder.generate(page, size, sort);
                page ++;
                //获取所有满足奖励金的排名数据
                Page<SaleRanking> rankingList = saleRankingDao.findByMonth(month, rr.getStartMoney(), pager);
                len = rankingList.getContent().size();
                insertData(rr, rankingList.getContent(), year, month);
            }
        }
    }

    private synchronized void insertData(RewardRule rule, List<SaleRanking> rankingList, String year, String month) {
        for(SaleRanking ranking : rankingList) {
            Reward r = new Reward();
            r.setAgentId(ranking.getAgentId());
            r.setAgentName(ranking.getAgentName());
            r.setAgentPhone(ranking.getAgentPhone());
            r.setCommissionMoney(ranking.getCommissionMoney());
            r.setCreateDay(NormalTools.curDate());
            r.setCreateLong(System.currentTimeMillis());
            r.setCreateTime(NormalTools.curDatetime());
            r.setCustomId(ranking.getCustomId());
            r.setCustomNickname(ranking.getCustomNickname());
            r.setCustomOpenid(ranking.getCustomOpenid());
            r.setExtraMoney(rebuildMoney(rule.getRewardRate()*ranking.getCommissionMoney())); //提成金额*奖励比例
            r.setProduceMonth(month);
            r.setProduceYear(year);
            r.setReceiptTimes(0); //已领取次数
            r.setReceiptMoney(0f); //已领取金额
            r.setStatus("0");
            r.setTotalTimes(buildTotalTimes(rule.getMonthRate())); //最多可领取几个月
            r.setSurplusTimes(r.getTotalTimes()); //剩余次数，初始时就是总可领取次数
            r.setOrderNo(ranking.getOrderNo());
            r.setSurplusMoney(r.getExtraMoney()); //初始时剩余领取的金额就是总奖金
            rewardDao.save(r);
        }
    }

    //生成可领取次数
    private Integer buildTotalTimes(Float monthRate) {
        return (int)(1/monthRate);
    }

    private Float rebuildMoney(double money) {
        DecimalFormat df = new DecimalFormat("#.00");
        return Float.parseFloat(df.format(money));
    }
}
