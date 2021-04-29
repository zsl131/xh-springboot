package com.zslin.core.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.model.ExpressConfig;
import com.zslin.core.dto.express.ExpressDetailDto;
import com.zslin.core.dto.express.ExpressResultDto;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流查询工具类
 */
@Component
public class ExpressTools {

    @Autowired
    private ExpressConfigTools expressConfigTools;

    /**
     * 查询物流信息
     * @param no 快递单号，如果是顺丰，则需要输入收件人或寄件人手机号后四位。例如：123456789:1234
     * @return
     */
    public String query(String no) {
        ExpressConfig config = expressConfigTools.getExpressConfig();
        String host = config.getUrl();
        String path = config.getPath();
        String method = "GET";
        //System.out.println("请先替换成自己的AppCode");
        String appcode = config.getAppCode();  // !!!替换填写自己的AppCode 在买家中心查看
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appcode); //格式为:Authorization:APPCODE 83359fd73fe11248385f570e3c139xxx
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("no", no);// !!! 请求参数
//        querys.put("type", "zto");// !!! 请求参数
        //JDK 1.8示例代码请在这里下载：  http://code.fegine.com/Tools.zip
        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 或者直接下载：
             * http://code.fegine.com/HttpUtils.zip
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             * 相关jar包（非pom）直接下载：
             * http://code.fegine.com/aliyun-jar.zip
             */
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            //System.out.println(response.toString());如不输出json, 请打开这行代码，打印调试头部状态码。
            //状态码: 200 正常；400 URL无效；401 appCode错误； 403 次数用完； 500 API网管错误
            //获取response的body
            String str = EntityUtils.toString(response.getEntity());
            //System.out.println(str); //输出json
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 查询物流信息
     * @param no 快递单号，如果是顺丰，则需要输入收件人或寄件人手机号后四位。例如：123456789:1234
     * @return
     */
    public ExpressResultDto query2Dto(String no) {
        String str = query(no);
        return query2DtoByStr(str);
    }

    public ExpressResultDto query2DtoByStr(String str) {
        if(str==null || "".equals(str)) {return null;}
        ExpressResultDto dto = null;
        String status = JsonTools.getJsonParam(str, "status");
        String msg = JsonTools.getJsonParam(str, "msg");
        if("0".equals(status)) { //如果没有出错
            String result = JsonTools.getJsonParam(str, "result");
            dto = JSONObject.toJavaObject(JSON.parseObject(result), ExpressResultDto.class);
            String list = JsonTools.getJsonParam(result, "list");
            JSONArray jsonArray = JsonTools.str2JsonArray(list);
            List<ExpressDetailDto> detailList = new ArrayList<>();
            for(int i=0;i<jsonArray.size();i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                detailList.add(new ExpressDetailDto(jsonObj.getString("time"), jsonObj.getString("status")));
            }
            dto.setDetailList(detailList);
        }
        if(dto==null) {
            dto = new ExpressResultDto();
        }
        dto.setStatus(status);
        dto.setMsg(msg);
        return dto;
    }
}
