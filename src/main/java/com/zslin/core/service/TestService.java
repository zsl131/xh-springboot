package com.zslin.core.service;

import com.zslin.business.dao.ISearchRecordDao;
import com.zslin.business.model.SearchRecord;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@Slf4j
public class TestService {

    @Autowired
    private ISearchRecordDao searchRecordDao;

    public JsonResult handler(String params) {
      log.info("testService.handler params: {}", params);
      return JsonResult.success("调用成功");
    }

    public JsonResult handler(String params, Integer b) {
        return JsonResult.success();
    }

    @Transactional
    public JsonResult add(String params) {
        SearchRecord sr = new SearchRecord();
        sr.setKeyword(RandomUtils.nextDouble()+"");
        sr.setCreateDay(NormalTools.curDate());
        sr.setCreateTime(NormalTools.curDatetime());
        sr.setCreateLong(System.currentTimeMillis());
        searchRecordDao.save(sr);
        if(true) {
//            throw new BusinessException(BusinessException.Code.DEFAULT_ERR_CODE, "测试出错");
//            throw new RuntimeException("测试出错，runtime");
        }
        return JsonResult.success("添加成功");
    }
}
