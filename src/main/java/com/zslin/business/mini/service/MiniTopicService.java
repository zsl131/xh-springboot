package com.zslin.business.mini.service;

import com.zslin.business.dao.ITopicDao;
import com.zslin.business.model.Topic;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.rabbit.RabbitNormalTools;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** 文章管理 */
@Service
public class MiniTopicService {

    @Autowired
    private ITopicDao topicDao;

    @Autowired
    private RabbitNormalTools rabbitNormalTools;

    public JsonResult show(String params) {
        String type = JsonTools.getJsonParam(params, "type");
        Integer id = JsonTools.getId(params);
        String sn = JsonTools.getJsonParam(params, "sn");
        Topic topic = null;
        if("sn".equalsIgnoreCase(type)) { //如果是通过sn获取对象
            topic = topicDao.findBySn(sn);
        } else {
            topic = topicDao.findOne(id);
        }

        if(topic!=null) {
            rabbitNormalTools.updateData("topicDao", "plusReadCount", 1, topic.getId());
        }
        return JsonResult.success().set("topic", topic);
    }
}
