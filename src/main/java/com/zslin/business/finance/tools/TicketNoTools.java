package com.zslin.business.finance.tools;

import com.zslin.business.finance.dao.IFinanceDetailDao;
import com.zslin.business.finance.dao.IFinanceRecordDao;
import com.zslin.business.finance.dto.NoDto;
import com.zslin.business.finance.model.FinanceDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zsl on 2019/1/5.
 */
@Component
public class TicketNoTools {

    @Autowired
    private IFinanceDetailDao financeDetailDao;

    @Autowired
    private IFinanceRecordDao financeRecordDao;

    public NoDto getNewRecordTicketNo(String month) {
        Integer tno = financeRecordDao.maxTicketNo(month);
        if(tno == null) {tno = 0;}
        tno += 1;
        StringBuffer sb = new StringBuffer();
        for(int i=(tno+"").length();i<4;i++) {
            sb.append("0");
        }
        sb.append(tno);
        String res = sb.toString();
        return new NoDto(tno, month+res);
    }

    /** 2019010002 */
    public String buildTicketNo(String month) {
        Integer tno = financeDetailDao.maxTicketNo(month);
        if(tno==null) {tno = 0;}
        tno += 1;
        StringBuffer sb = new StringBuffer();
        for(int i=(tno+"").length();i<4;i++) {
            sb.append("0");
        }
        sb.append(tno);
        String res = sb.toString();
        saveDetail(month, res);
        return res;
    }

    private void saveDetail(String month, String tno) {
        FinanceDetail fd = new FinanceDetail();
        fd.setRecordYear(month.substring(0, 4));
        fd.setRecordMonth(month);
        fd.setTicketNo(month+tno);
        fd.setTno(Integer.parseInt(tno));
        fd.setStatus("0"); //初始状态
        financeDetailDao.save(fd);
    }
}
