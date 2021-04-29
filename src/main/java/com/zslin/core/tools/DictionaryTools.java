package com.zslin.core.tools;

import com.alibaba.fastjson.JSONObject;
import com.zslin.core.dto.DictionaryDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsl on 2018/8/7.
 * 数据字典处理工具类
 */
public class DictionaryTools<E> {

    public List<DictionaryDto> buildDictionaryDtoList(List<E> objectList) {
        List<DictionaryDto> result = new ArrayList<>();
        for(Object obj : objectList) {
            JSONObject jsonObj = JSONObject.parseObject(JSONObject.toJSONString(obj));
            result.add(new DictionaryDto(jsonObj.getInteger("id"), jsonObj.getString("name")));
        }
        return result;
    }
}
