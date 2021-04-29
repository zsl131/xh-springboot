package com.zslin.business.wx.tools;

import com.alibaba.fastjson.JSONObject;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 微信素材管理
 */
@Component
public class WxMediaTools {

    @Autowired
    private WxAccessTokenTools wxAccessTokenTools;

    public String queryMedias() {
        return queryMedias("news", 0, 20);
    }

    public String queryMedias( Integer offset, Integer count) {
        return queryMedias("news", offset, count);
    }

    public String queryMedias(String type, Integer offset, Integer count) {
        String url = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token="+wxAccessTokenTools.getAccessToken();
        JSONObject jsonObj = WeixinUtil.httpRequest(url, "POST", buildJson(type, offset, count));
        return jsonObj.toJSONString();
    }

    private String buildJson(String type, Integer offset, Integer count) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\"type\":\""+type+"\",")
                .append("\"offset\":"+offset+",")
                .append("\"count\":"+count);
        sb.append("}");
        //System.out.println(sb.toString());
        return sb.toString();
    }
}
