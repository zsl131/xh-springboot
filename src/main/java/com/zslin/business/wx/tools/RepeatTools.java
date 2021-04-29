package com.zslin.business.wx.tools;

import com.zslin.core.cache.CacheTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by 钟述林 393156105@qq.com on 2017/1/24 11:47.
 */
@Component
public class RepeatTools {

    @Autowired
    private CacheTools cacheTools;

    public boolean hasRepeat(String openid, String time) {
        String name = openid+"-"+time;
        return hasRepeat(name);
    }

    public boolean hasRepeat(String msgId, String openid, String time) {
        String name = (msgId==null || "".equals(msgId))?(openid+"-"+time):msgId;
        return hasRepeat(name);
    }

    public boolean hasRepeat(String name) {
        boolean res = cacheTools.exists(name);
        if(!res) {
            cacheTools.putKey(name, 1, 15);
        }
        return res;
    }
}
