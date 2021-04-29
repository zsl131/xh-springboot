package com.zslin.business.tools;

import com.zslin.business.dao.IMediumDao;
import com.zslin.business.model.Medium;
import com.zslin.core.qiniu.tools.QiniuTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MediumTools {

    @Autowired
    private IMediumDao mediumDao;

    @Autowired
    private QiniuTools qiniuTools;

    /**
     * 删媒介
     * @param mediumId 媒介ID
     * @return
     */
    public boolean deleteMedium(Integer mediumId) {
        try {
            Medium m = mediumDao.findOne(mediumId);
            qiniuTools.deleteFile(m.getQiniuKey());
            mediumDao.delete(m);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
