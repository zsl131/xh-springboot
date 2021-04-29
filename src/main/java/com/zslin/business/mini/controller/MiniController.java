package com.zslin.business.mini.controller;

import com.zslin.business.mini.utils.MiniDataTools;
import com.zslin.business.mini.utils.MiniEventTools;
import com.zslin.business.mini.utils.MiniSignTools;
import com.zslin.business.wx.tools.RepeatTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by zsl on 2018/7/20.
 */
@Controller
@RequestMapping(value = "mini")
public class MiniController {

    @Autowired
    private MiniSignTools miniSignTools;

    @Autowired
    private MiniEventTools miniEventTools;

    @Autowired
    private MiniDataTools miniDataTools;

    @Autowired
    private RepeatTools repeatTools;


    @GetMapping(value = "root")
    public @ResponseBody String root(String signature, String timestamp, String nonce, String echostr, HttpServletResponse response) {
        //System.out.println("===========MiniController echostr===========>"+echostr);
        try {
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (miniSignTools.checkSignature(signature, timestamp, nonce)) {
                //System.out.println("------------MiniController  check success---------");
                return echostr;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return echostr;
    }

    @PostMapping(value = "root")
    public @ResponseBody String root(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/json");
        response.setCharacterEncoding("UTF-8");

        try {
            request.setCharacterEncoding("UTF-8");

            Element root = miniEventTools.getMessageEle(request);

            Node fromUser = root.getElementsByTagName("FromUserName").item(0);
            Node createTime = root.getElementsByTagName("CreateTime").item(0);
            Node msgType = root.getElementsByTagName("MsgType").item(0);
//            Node content = root.getElementsByTagName("Content").item(0);
//            Node event = root.getElementsByTagName("Event").item(0);
//            Node eventKey = root.getElementsByTagName("EventKey").item(0);
            Node msgId = root.getElementsByTagName("MsgId").item(0);
           // String builderName = root.getElementsByTagName("ToUserName").item(0).getTextContent(); //开发者微信号

            String msgIdStr = msgId!=null?msgId.getTextContent():null; //消息ID
            String fromOpenid = fromUser.getTextContent(); //用户的openid
            String cTime = createTime.getTextContent(); //创建时间
           // String msgTypeStr = msgType.getTextContent(); //事件类型

            if(repeatTools.hasRepeat(msgIdStr, fromOpenid, cTime)) { //如果重复
                return "success";
            } else {
                miniDataTools.onEventText(root);
                /*if("text".equalsIgnoreCase(msgTypeStr)) { //文本
                    String conStr = content.getTextContent(); //文本
                } else if("image".equalsIgnoreCase(msgTypeStr)) { //图片
                    String picUrl = root.getElementsByTagName("PicUrl").item(0).getTextContent();
                    String MediaId = root.getElementsByTagName("MediaId").item(0).getTextContent();
                }*/
               /* try { System.out.println("content: "+ content.getTextContent()); } catch (Exception e) { e.printStackTrace(); }
                try { System.out.println("event: "+ event.getTextContent()); } catch (Exception e) { e.printStackTrace(); }
                try { System.out.println("eventKey: "+ eventKey.getTextContent()); } catch (Exception e) { e.printStackTrace(); }
                try { System.out.println("msgTypeStr: "+ msgTypeStr); } catch (Exception e) { e.printStackTrace(); }*/
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
