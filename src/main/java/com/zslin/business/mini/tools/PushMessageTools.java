package com.zslin.business.mini.tools;

import com.zslin.business.mini.dto.CustomMessageDto;
import com.zslin.business.mini.dto.MsgDto;
import com.zslin.business.mini.dto.PushMsgDto;
import com.zslin.business.mini.dto.SingleDataDto;
import com.zslin.core.common.NormalTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class PushMessageTools {

    @Autowired
    private AccessTokenTools  accessTokenTools;

    public void sendTextMsg(String toUser, String text) {

        RestTemplate template = new RestTemplate();
        //ResponseEntity<String> entity = template.postForEntity(url, "con", String.class);

//        String json = createTextMsgCon(toUser, text);
//        String json = "";
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+accessTokenTools.getAccessToken();
        ResponseEntity<String> entity = template.postForEntity(url, buildTextMsgCon(toUser, text), String.class);
        System.out.println(NormalTools.curDatetime() +"-----PushMessageTools-----");
        System.out.println(entity.getBody());
        /*JSONObject jsonObj = WeixinUtil.httpRequest(url, "POST", json);
        System.out.println("===PushMessageTools.sendTextMsg==="+jsonObj.toJSONString());*/
        //String code = JsonTools.getJsonParam(jsonObj.toString(), "errcode");
    }

    private CustomMessageDto buildTextMsgCon(String toUser, String content) {
        CustomMessageDto dto = new CustomMessageDto();
        Map<String, String> cons = new HashMap<>();
        cons.put("content", content);
        dto.setTouser(toUser);
        dto.setText(cons);
        return dto;
    }

    private String createTextMsgCon(String toUser, String content) {
        StringBuffer sb = new StringBuffer();
        sb.append("{\"touser\":\"").append(toUser).append("\",")
                .append("\"msgtype\":\"text\",\"text\":")
                .append("{\"content\":\"").append(content).append("\"}}");
        //System.out.println("----PushMessageTools.createTextMsgCon---"+sb.toString());
        return sb.toString();
    }

    public void push(String toUser, String tempId, String page, MsgDto... content) {
        String accessToken = accessTokenTools.getAccessToken();
        //System.out.println("------"+accessToken);
        RestTemplate template = new RestTemplate();
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token="+accessToken;
//        String str = template.getForObject(url, String.class);
        PushMsgDto con = buildCon(toUser, tempId, page, content);
        /*String str = template.postForObject(url, buildCon(toUser, tempId, page, content), String.class);
        System.out.println(str);*/
        ResponseEntity<String> entity = template.postForEntity(url, con, String.class);
       // System.out.println(entity.getBody());
    }

    private PushMsgDto buildCon(String toUser, String tempId, String page, MsgDto... content) {
        PushMsgDto dto = new PushMsgDto();
        Map<String, SingleDataDto> cons = new HashMap<>();
        for(MsgDto d : content) {
            cons.put(d.getKey(), new SingleDataDto(d.getValue()));
        }
        dto.setData(cons);
        dto.setPage(page);
        dto.setTemplate_id(tempId);
        dto.setTouser(toUser);
        return dto;
    }
}
