package com.zslin.core.tools;

import com.alibaba.fastjson.JSONObject;
import com.zslin.core.dto.QueryListConditionDto;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.repository.SimpleSpecificationBuilder;
import com.zslin.core.repository.SpecificationOperator;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by zsl on 2018/7/10.
 * 从客户端提交查询数据的工具类
 */
public class QueryTools {

    public static QueryTools getInstance() {
        return new QueryTools();
    }

    public Specification buildSearch(SpecificationOperator... opts) {
        SimpleSpecificationBuilder builder = new SimpleSpecificationBuilder();
        builder.add(opts); //先添加
        return builder.generate();
    }

    public Specification buildSearch(List<QueryListConditionDto> conditionDtoList, SpecificationOperator... ops) {
        SimpleSpecificationBuilder builder = new SimpleSpecificationBuilder();
        builder.add(ops); //先添加
        for(QueryListConditionDto dto : conditionDtoList) {
            try {
                builder.add(dto.getKey(), dto.getMatch(), dto.getValue());
            } catch (Exception e) {
            }
        }
        return builder.generate();
    }

    public static QueryListDto buildQueryListDto(String params) {
//        System.out.println("====buildQueryListDto:::"+params);
        JSONObject paramObj = JsonTools.str2JsonObj(params);

        Integer page = 0;
        try {
            page = paramObj.getInteger("page");
        } catch (Exception e) {
        }
        Integer size = 15;
        try {
            size = paramObj.getInteger("size");
        } catch (Exception e) {
        }
        String sort = null;
        try {
            sort = paramObj.getString("sort");
        } catch (Exception e) {
        }
//        System.out.println("======="+sort);
        List<QueryListConditionDto> conList = new ArrayList<>();
        //以下划线开头的都需要在数据库中查询
        for(Object key:paramObj.keySet()) {
            String keyStr = key.toString();
            if(keyStr.startsWith("_")) {
                conList.add(new QueryListConditionDto(keyStr.substring(1,keyStr.length()), "eq", paramObj.getString(keyStr)));
            }
        }
        String conditions = JsonTools.getJsonParam(params, "conditions");
        if(conditions!=null && !"".equals(conditions)) {
            try {
//                JSONArray jsonArray = JsonTools.str2JsonArray(conditions);
                JSONObject jsonObj = JsonTools.str2JsonObj(conditions);
                Set<String> keys = jsonObj.keySet();
                for(String keyAndMatch : keys) {
//                    String keyAndMatch = (String) keys.next();
                    String key , match ;
                    if(keyAndMatch.indexOf("_")>0) {
                        key = keyAndMatch.split("_")[0];
                        match = keyAndMatch.split("_")[1];
                    } else {
                        key = keyAndMatch; match = "eq";
                    }
                    String value = jsonObj.get(keyAndMatch).toString();
                    if(value!=null && !"".equals(value.trim()) && !"*".equals(value) && !"?".equals(value)) { //星号问号需要忽略
                        conList.add(new QueryListConditionDto(key, match, value));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new QueryListDto(page, size, sort, conList);
    }
}
