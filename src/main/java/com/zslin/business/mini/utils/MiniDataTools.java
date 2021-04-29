package com.zslin.business.mini.utils;

import com.zslin.business.dao.ICustomerDao;
import com.zslin.business.mini.dao.ICustomMessageDao;
import com.zslin.business.mini.model.CustomMessage;
import com.zslin.business.model.Customer;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.business.wx.tools.WxAccountTools;
import com.zslin.core.common.NormalTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by 钟述林 393156105@qq.com on 2017/1/24 22:26.
 */
@Component
@HasTemplateMessage
public class MiniDataTools {

//    @Autowired
//    private MiniConfigTools miniConfigTools;

//    @Autowired
//    private ConfigTools configTools;

   // @Autowired
    //private WxAccountTools wxAccountTools;

   // @Autowired
   // private RabbitTemplate rabbitTemplate;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @Autowired
    private ICustomerDao customerDao;

    @Autowired
    private ICustomMessageDao customMessageDao;

    /**
     * 添加文本内容
     * @return
     */
    @TemplateMessageAnnotation(name = "业务咨询通知", keys = "咨询姓名-联系方式-咨询日期-咨询类型-咨询详情")
    public void onEventText(Element root) {
        Node fromUser = root.getElementsByTagName("FromUserName").item(0);
        Node createTime = root.getElementsByTagName("CreateTime").item(0);
        Node msgType = root.getElementsByTagName("MsgType").item(0);
        Node content = root.getElementsByTagName("Content").item(0);
        //Node event = root.getElementsByTagName("Event").item(0);
        //Node eventKey = root.getElementsByTagName("EventKey").item(0);
        Node msgId = root.getElementsByTagName("MsgId").item(0);
        //String builderName = root.getElementsByTagName("ToUserName").item(0).getTextContent(); //开发者微信号

        String msgIdStr = msgId!=null?msgId.getTextContent():null; //消息ID
        String fromOpenid = fromUser.getTextContent(); //用户的openid
        String cTime = createTime.getTextContent(); //创建时间
        String msgTypeStr = msgType.getTextContent(); //事件类型
        String conStr = content==null?"":NormalTools.rebuildUTF8MB4(content.getTextContent()); //内容
        if(conStr==null || "".equals(conStr.trim())) {return;}

        CustomMessage msg = new CustomMessage();
        msg.setCreateLong(System.currentTimeMillis());
        msg.setCreateDay(NormalTools.curDate());
        msg.setCreateTime(NormalTools.curDatetime());
        msg.setOpenid(fromOpenid);
        msg.setMsgId(msgIdStr);
        msg.setCreateTimeMini(cTime);
        msg.setMsgType(msgTypeStr);

        if("text".equalsIgnoreCase(msgTypeStr)) {
            msg.setContent(conStr);
        } else if("image".equalsIgnoreCase(msgTypeStr)) {
            String picUrl = root.getElementsByTagName("PicUrl").item(0).getTextContent();
            String mediaId = root.getElementsByTagName("MediaId").item(0).getTextContent();
            msg.setMediaId(mediaId);
            msg.setPicUrl(picUrl);
        }

        Customer customer = customerDao.findByOpenid(fromOpenid);
        if(customer!=null) {
            msg.setNickname(customer.getNickname());
            msg.setHeadImgUrl(customer.getHeadImgUrl());
        }

        customMessageDao.save(msg);
        sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "业务咨询通知", "", "收到新客服消息",
                TemplateMessageTools.field("咨询姓名", msg.getNickname()),
                TemplateMessageTools.field("联系方式", "-"),
                TemplateMessageTools.field("咨询日期", NormalTools.curDatetime()),
                TemplateMessageTools.field("咨询类型", msgTypeStr),
                TemplateMessageTools.field("咨询详情", conStr),
                TemplateMessageTools.field("请及时登陆后台查阅处理"));
    }

}
