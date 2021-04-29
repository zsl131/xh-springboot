package com.zslin.business.wx.tools;

import com.alibaba.fastjson.JSONObject;
import com.zslin.business.tools.BindCodeTools;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasScore;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.dao.IFeedbackDao;
import com.zslin.business.wx.dao.IWxAccountDao;
import com.zslin.business.wx.model.Feedback;
import com.zslin.business.wx.model.WxAccount;
import com.zslin.core.common.NormalTools;
import com.zslin.core.tools.ConfigTools;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 钟述林 393156105@qq.com on 2017/1/24 22:26.
 */
@Component
@HasTemplateMessage
@HasScore
public class DatasTools {


    @Autowired
    private WxConfigTools wxConfigTools;

    @Autowired
    private ConfigTools configTools;

    @Autowired
    private WxAccountTools wxAccountTools;

    @Autowired
    private IWxAccountDao wxAccountDao;

//    @Autowired
//    private EventTools eventTools;

    @Autowired
    private TemplateMessageTools templateMessageTools;

    @Autowired
    private IFeedbackDao feedbackDao;

    @Autowired
    private ExchangeTools exchangeTools;

   /* @Autowired
    private ScoreTools scoreTools;*/

    /*@Autowired
    private INoticeDao noticeDao;*/

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BindCodeTools bindCodeTools;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    /** 当用户取消关注时 */
    public void onUnsubscribe(String openid) {
        wxAccountDao.updateStatus(openid, "0");
    }

    /**
     * 添加文本内容
     * @param openid 用户Openid
     * @param builderName 开发者微信号
     * @param content 具体内容
     * @return
     */
    @TemplateMessageAnnotation(name = "业务咨询通知", keys = "咨询姓名-联系方式-咨询日期-咨询类型-咨询详情")
    public String onEventText(String openid, String builderName, String content) {
        content = NormalTools.rebuildUTF8MB4(content); //转换
        if(content==null || "".equals(content.trim()) || "?".equals(content.trim())
                || "？".equals(content.trim()) || "1".equals(content.trim())
                || "help".equals(content.toLowerCase().trim())) { //帮助
            return WeixinXmlTools.createTextXml(openid, builderName, "HELP");
        } else if(isBindWxMini(content)) { //微信和小程序绑定操作
            String res = bindCodeTools.bindWxMini(content, openid);
            return WeixinXmlTools.createTextXml(openid, builderName, res);
        } else {
            Feedback f = new Feedback();
            f.setCreateLong(System.currentTimeMillis());
            f.setCreateDay(NormalTools.curDate());
            f.setCreateTime(NormalTools.curDatetime());
            f.setOpenid(openid);
            f.setStatus("0");
            f.setContent(content);
            WxAccount a = wxAccountDao.findByOpenid(openid);
            if (a != null) {
                f.setAccountId(a.getId());
                f.setNickname(a.getNickname());
                f.setHeadImgUrl(a.getHeadImgUrl());
            }
            feedbackDao.save(f);

//            List<String> adminOpenids = accountService.findOpenid(AccountTools.ADMIN);
//            List<String> adminOpenids = wxAccountTools.getOpenid(wxAccountTools.ADMIN);
            /*SendMessageDto smd = new SendMessageDto("在线反馈", adminOpenids, "", "您有一条新的反馈信息！", "反馈日期="+NormalTools.getNow("yyyy-MM-dd HH:mm"), "反馈用户="+f.getNickname(), "反馈内容="+content);
            rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.DIRECT_ROUTING, smd);*/

            //咨询姓名-联系方式-咨询日期-咨询类型-咨询详情
            sendTemplateMessageTools.send2Manager(WxAccountTools.ADMIN, "业务咨询通知", "", "收到新留言",
                    TemplateMessageTools.field("咨询姓名", a.getNickname()),
                    TemplateMessageTools.field("联系方式", "-"),
                    TemplateMessageTools.field("咨询日期", NormalTools.curDatetime()),
                    TemplateMessageTools.field("咨询类型", "公众号留言"),
                    TemplateMessageTools.field("咨询详情", content),
                    TemplateMessageTools.field("请及时登陆后台查阅处理"));

            return "";
        }
    }

    ///判断是否是绑定小程序微信
    private boolean isBindWxMini(String content) {
        try {
            if(content!=null && content.length()==4) {
                if(Integer.parseInt(content)>0) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /** 当用户关注时 */
    public synchronized void onSubscribe(String openid) {
        Integer id = (Integer) wxAccountDao.queryByHql("SELECT a.id FROM WxAccount a WHERE a.openid=?1 ", openid);
        WxAccount a ;
        boolean isFirst = false;
        if(id==null || id<=0) { //说明初次关注
            a = new WxAccount();
            a.setStatus("1");
            a.setType("0");
            a.setOpenid(openid);
            a.setCreateLong(System.currentTimeMillis());
            a.setCreateDay(NormalTools.curDate());
            a.setCreateTime(NormalTools.curDatetime());
            isFirst = true;
        } else {
//            accountService.updateStatus(id, "1");
            a = wxAccountDao.findOne(id);
            if("0".equals(a.getStatus())) {
                a.setFollowDay(NormalTools.curDate());
                a.setStatus("1");
            }
        }
        a.setFollowDay(NormalTools.curDate());
        a.setFollowLong(System.currentTimeMillis());
        a.setFollowTime(NormalTools.curDatetime());

        JSONObject jsonObj = exchangeTools.getUserInfo(openid);
        if(jsonObj!=null) {
            String jsonStr = jsonObj.toString();
            if(jsonStr.indexOf("errcode")<0 && jsonStr.indexOf("errmsg")<0) {
                String nickname = "";
                try {
                    nickname = jsonObj.getString("nickname");
                    nickname = nickname.replaceAll("[^\\u0000-\\uFFFF]", ""); //替换utf8mb4字条
                } catch (Exception e) {
                }
                a.setHeadImgUrl(jsonObj.getString("headimgurl"));
                a.setNickname(nickname);
                a.setOpenid(openid);
//                a.setSex(jsonObj.getInt("sex")+"");
            }
        }
        wxAccountDao.save(a);

        if(isFirst) {
            plusScore(openid); //加积分
        }
    }

    /*@ScoreAnnotation("关注得积分")*/
    private void plusScore(String openid) {
        /*scoreTools.plusScoreByThread("关注得积分", openid);*/
    }

    /** 同步更新微信用户信息，主要是昵称、头像、性别 */
    public void updateAccount(Integer accountId) {
        WxAccount a = wxAccountDao.findOne(accountId);
        JSONObject jsonObj = exchangeTools.getUserInfo(a.getOpenid());
        if(jsonObj!=null) {
            String jsonStr = jsonObj.toString();
            if(jsonStr.indexOf("errcode")<0 && jsonStr.indexOf("errmsg")<0) {
                String nickname = "";
                try {
                    nickname = jsonObj.getString("nickname");
                    nickname = nickname.replaceAll("[^\\u0000-\\uFFFF]", ""); //替换utf8mb4字符
                } catch (Exception e) {
                }
                a.setHeadImgUrl(jsonObj.getString("headimgurl"));
                a.setNickname(nickname);
//                a.setSex(jsonObj.getInt("sex")+"");
            }
        }
        wxAccountDao.save(a);

//        updateRelation(a); //更新所有关联数据
    }

    /** 构建关注时的数据 */
   /* public String buildSubscribeStr(String toUser, String fromUser) {
        List<Notice> articleList = noticeDao.findNeedSend(SimpleSortBuilder.generateSort("orderNo_a"));
        return WeixinXmlTools.buildSubscribeStr(toUser, fromUser, articleList, wxConfigTools.getWxConfig().getUrl());
//        return "";
    }*/

    /*private void updateRelation(Account a) {
        commentService.update(a.getNickname(), a.getHeadimgurl(), a.getOpenid());
        feedbackService.update(a.getNickname(), a.getHeadimgurl(), a.getOpenid());
        activityRecordService.update(a.getNickname(), a.getHeadimgurl(), a.getOpenid());
        walletDetailService.update(a.getNickname(), a.getOpenid());
        qrcodeService.update(a.getNickname(), a.getHeadimgurl(), a.getOpenid());
    }*/

    /**
     * 检测在数据库中是否存在该用户
     * @param fromOpenid
     */
    public void checkWxAccount(String fromOpenid) {
        new Thread(()->onSubscribe(fromOpenid)).start();
    }
}
