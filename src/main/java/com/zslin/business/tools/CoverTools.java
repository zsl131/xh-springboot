package com.zslin.business.tools;

import com.zslin.business.dao.IMediumDao;
import com.zslin.business.dto.CoverDto;
import com.zslin.business.model.Medium;
import com.zslin.core.repository.SimpleSortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 封面图片处理工具类
 */
@Component
public class CoverTools {

    @Autowired
    private IMediumDao mediumDao;

    public CoverDto buildUrls(String randomTicket, String objType, String isFirst, String order) {
        Sort sort = SimpleSortBuilder.generateSort(order==null?"id":order);
        List<Medium> list = mediumDao.findByTicketAndObjClassNameAndIsFirst(randomTicket, objType, isFirst, sort);
        return buildUrls(list);
    }

    public CoverDto buildUrls(Integer objId, String objType, String isFirst, String order) {
        Sort sort = SimpleSortBuilder.generateSort(order==null?"id":order);
        List<Medium> list = mediumDao.findByObjIdAndObjClassNameAndIsFirst(objId, objType, isFirst, sort);
        return buildUrls(list);
    }

    private CoverDto buildUrls(List<Medium> list) {
        String sep = ",";
        StringBuffer pSb = new StringBuffer();
        StringBuffer vSb = new StringBuffer();
        for(Medium m : list) {
            if("image".equalsIgnoreCase(m.getType())) {
                pSb.append(m.getRootUrl()+m.getQiniuKey()).append(sep);
            } else if("video".equalsIgnoreCase(m.getType())) {
                vSb.append(m.getRootUrl()+m.getQiniuKey()).append(sep);
            }
        }
        String pStr = pSb.toString();
        if(pStr.endsWith(sep)) {pStr = pStr.substring(0, pStr.length()-1);}

        String vStr = vSb.toString();
        if(vStr.endsWith(sep)) {vStr = vStr.substring(0, vStr.length()-1);}
        return new CoverDto(pStr, vStr);
    }
}
