package com.zslin.business.settlement.tools;

import com.zslin.business.dao.ICustomCommissionRecordDao;
import com.zslin.business.settlement.dao.ISaleRankingDao;
import com.zslin.business.settlement.dto.RankingDto;
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
 * 排名工具类
 */
@Component("rankingTools")
public class RankingTools {

    @Autowired
    private ICustomCommissionRecordDao commissionRecordDao;

    @Autowired
    private ISaleRankingDao saleRankingDao;

    /**
     * 生成排名
     *      * 原则上是每月1号凌晨生成排名
     */
    public void buildRanking() {
        String preMonth = NormalTools.getMonth("yyyyMM", -1); //上一个月
        buildRanking(preMonth);
    }

    /**
     * 生成排名
     * 原则上是每月1号凌晨生成排名
     * @param month 月份，格式：yyyyMM
     */
    public void buildRanking(String month) {
        //System.out.println("-----------> month:"+month);
        Sort sort = SimpleSortBuilder.generateSort("totalMoney_d", "totalCount_d");

        Long count = saleRankingDao.queryCount(month);
        if(count>0) {return ;} //如果已经生成过就不能再重复生成

        String year = month.substring(0, 4); //年

//        List<RankingDto> dtoList = new ArrayList<>();
        int page = 0, size = 200, len = size;
        while(len>=size) {
            Pageable pager = SimplePageBuilder.generate(page, size, sort);
            page ++; //调用一次页码加1
            Page<RankingDto> pageList = commissionRecordDao.queryRanking(month, pager);
            List<RankingDto> dtoList = pageList.getContent();
            len = dtoList.size();

            insertRanking(dtoList, year, month);
        }
    }

    private synchronized void insertRanking(List<RankingDto> dtoList, String year, String month) {
        Integer maxOrderNo = saleRankingDao.queryMaxOrderNo(month);
        if(maxOrderNo==null) {maxOrderNo = 0;}

        maxOrderNo ++;

        //System.out.println("==="+maxOrderNo);
        for(RankingDto dto : dtoList) {
            SaleRanking sr = new SaleRanking();
            sr.setAgentId(dto.getAgentId());
            sr.setAgentName(dto.getAgentName());
            sr.setAgentPhone(dto.getAgentPhone());
            sr.setBelongMonth(month);
            sr.setBelongYear(year);
            sr.setCommissionMoney(rebuildMoney(dto.getCommissionMoney()));
            sr.setCustomId(dto.getCustomId());
            sr.setCustomNickname(dto.getCustomNickname());
            sr.setCustomOpenid(dto.getOpenid());
            sr.setSpecsCount((int)dto.getSpecsCount());
            sr.setOrderNo(maxOrderNo++); //TODO 设置排序
            //System.out.println("----------"+maxOrderNo);
//            sr.setTotalMoney();
            saleRankingDao.save(sr);
        }
    }

    private Float rebuildMoney(double money) {
        DecimalFormat df = new DecimalFormat("#.00");
        return Float.parseFloat(df.format(money));
    }
}
