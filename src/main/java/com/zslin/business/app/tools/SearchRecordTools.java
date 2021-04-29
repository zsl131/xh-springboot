package com.zslin.business.app.tools;

import com.zslin.business.dao.ISearchRecordDao;
import com.zslin.business.model.SearchRecord;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 小程序搜索工具类
 */
@Component("searchRecordTools")
public class SearchRecordTools {

    @Autowired
    private ISearchRecordDao searchRecordDao;

    /**
     * 记录搜索信息
     * @param requestParams
     */
    public void record(String requestParams) {
        WxCustomDto custom = JsonTools.getCustom(requestParams);
        String keyword = JsonTools.getJsonParam(requestParams, "keyword");
        SearchRecord sr = searchRecordDao.findByKeywordAndCustomId(keyword, custom.getCustomId());
        if(sr==null) {
            sr = new SearchRecord();
            sr.setCreateLong(System.currentTimeMillis());
            sr.setCreateTime(NormalTools.curDatetime());
            sr.setCreateDay(NormalTools.curDate());
            sr.setKeyword(keyword);
            sr.setCount(1);
            sr.setNickname(custom.getNickname());
            sr.setOpenid(custom.getOpenid());
            sr.setUnionid(custom.getUnionid());
            sr.setCustomId(custom.getCustomId());

            searchRecordDao.save(sr);
        } else {
            sr.setCount(sr.getCount()+1);
            sr.setUpdateDay(NormalTools.curDate());
            sr.setUpdateTime(NormalTools.curDatetime());
            sr.setUpdateLong(System.currentTimeMillis());
            searchRecordDao.save(sr);
        }
    }
}
