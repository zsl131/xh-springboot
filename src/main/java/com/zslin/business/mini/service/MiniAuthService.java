package com.zslin.business.mini.service;

import com.zslin.business.dao.IAgentDao;
import com.zslin.business.dao.ICustomerDao;
import com.zslin.business.mini.dao.IMiniConfigDao;
import com.zslin.business.mini.dao.ISessionKeyDao;
import com.zslin.business.mini.dto.NewCustomDto;
import com.zslin.business.mini.model.MiniConfig;
import com.zslin.business.mini.model.SessionKey;
import com.zslin.business.mini.tools.CustomerTools;
import com.zslin.business.mini.tools.MiniUtils;
import com.zslin.business.model.Customer;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.qiniu.tools.QiniuTools;
import com.zslin.core.rabbit.RabbitNormalTools;
import com.zslin.core.tools.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
@Explain(name = "小程序授权管理", notes = "获取小程序用户信息")
public class MiniAuthService {

    @Autowired
    private IMiniConfigDao miniConfigDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitNormalTools rabbitNormalTools;

    @Autowired
    private ISessionKeyDao sessionKeyDao;

    @Autowired
    private IAgentDao agentDao;

    @Autowired
    private CustomerTools customerTools;

    /** 重新更新昵称和头像 */
    public JsonResult reloadUser(String params) {
        String code = JsonTools.getJsonParam(params, "code");
        String enc = JsonTools.getJsonParam(params, "encryptedData"); //
        String iv = JsonTools.getJsonParam(params, "iv");

        if(NormalTools.isNullOr(code, enc, iv)) {
            throw new BusinessException(BusinessException.Code.PARAM_NULL, "code、encryptedData、iv三者均不能为空");
        }

        MiniConfig config = miniConfigDao.loadOne();
        if(config==null || NormalTools.isNullOr(config.getAppid(), config.getAppSecret())) {
            throw new BusinessException(BusinessException.Code.CONFIG_NULL, "小程序未配置或Appid、AppSecret为空");
        }

        RestTemplate template = new RestTemplate();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+config.getAppid()
                +"&secret="+config.getAppSecret()+"&js_code="+code+"&grant_type=authorization_code";
        String str = template.getForObject(url, String.class);
//        System.out.println("====================================");
//        log.info(str);
        String openid = JsonTools.getJsonParam(str, "openid");
        String sessionKey = JsonTools.getJsonParam(str, "session_key");
        NewCustomDto dto = MiniUtils.decryptionUserInfo(enc, sessionKey, iv);
        Customer customer = customerDao.findByOpenid(openid);

        Integer customId = customer.getId();
        String nickname = NormalTools.rebuildUTF8MB4(dto.getNickName());
        String headimg = dto.getAvatarUrl();
        if(headimg!=null && !"".equals(headimg)) { //如果有头像
            headimg = qiniuTools.uploadCustomerHeadImg(headimg, "head-"+customId+"-"+(UUID.randomUUID().toString())+".jpg");
        }
        //更新信息
        rabbitNormalTools.updateData("customerTools", "updateUserInfo", nickname, headimg, customer.getId());
//        customer.setNickname();
        customer.setNickname(nickname);
        customer.setHeadImgUrl(headimg);
        return JsonResult.success("微信同步成功").set("customer", customer);
    }

    @ExplainOperation(name = "获取微信用户信息", params = {
            @ExplainParam(value = "code", name = "loginCode", require = true, example = "通过uni.login获取"),
            @ExplainParam(value = "encryptedData", name = "encryptedData", require = true, example = "通过uni.getUserInfo获取"),
            @ExplainParam(value = "iv", name = "iv", require = true, example = "通过uni.getUserInfo获取"),
    }, back = {
            @ExplainReturn(field = "custom", notes = "返回已获取的用户对象信息")
    })
    public JsonResult getUserInfo(String params) throws BusinessException {
        //System.out.println(params);
        //NewCustomDto dto = JSONObject.toJavaObject(JSON.parseObject(params), NewCustomDto.class);
        String code = JsonTools.getJsonParam(params, "code");
        String enc = JsonTools.getJsonParam(params, "encryptedData"); //
        String iv = JsonTools.getJsonParam(params, "iv");

        if(NormalTools.isNullOr(code, enc, iv)) {
            throw new BusinessException(BusinessException.Code.PARAM_NULL, "code、encryptedData、iv三者均不能为空");
        }

        MiniConfig config = miniConfigDao.loadOne();
        if(config==null || NormalTools.isNullOr(config.getAppid(), config.getAppSecret())) {
            throw new BusinessException(BusinessException.Code.CONFIG_NULL, "小程序未配置或Appid、AppSecret为空");
        }
        //log.info("code:::"+code);
        //log.info(config.toString());
        RestTemplate template = new RestTemplate();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+config.getAppid()
                +"&secret="+config.getAppSecret()+"&js_code="+code+"&grant_type=authorization_code";
        String str = template.getForObject(url, String.class);
//        System.out.println("====================================");
//        log.info(str);
        String openid = JsonTools.getJsonParam(str, "openid");
        String sessionKey = JsonTools.getJsonParam(str, "session_key");
        NewCustomDto dto = MiniUtils.decryptionUserInfo(enc, sessionKey, iv);

        new Thread(new Runnable() {
            @Override
            public void run() {
                addOrUpdateSessionKey(0, openid, sessionKey, code);
            }
        }).start();
        if(openid!=null && !"".equals(openid)) {
            //System.out.println(str);

            Integer leaderId = JsonTools.getParamInteger(params, "leaderId");
            String leaderNickname = JsonTools.getJsonParam(params, "leaderNickname");
            String leaderOpenid = JsonTools.getJsonParam(params, "leaderOpenid");

            Customer customer = new Customer();
            customer.setHeadImgUrl(dto.getAvatarUrl());
            customer.setNickname(NormalTools.rebuildUTF8MB4(dto.getNickName()));
            customer.setOpenid(openid);
            customer.setSex(dto.getGender()==1?"1":"2");
            customer.setStatus("1");
            customer.setUnionid(dto.getUnionId());
            customer.setLeaderId(leaderId);
            customer.setLeaderNickname(leaderNickname);
            customer.setLeaderOpenid(leaderOpenid);


            //rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.DIRECT_ROUTING, customer);

            return JsonResult.success("获取成功").set("custom", handlerCustomer(customer));
        } else {
            log.error("获取openid异常:::: "+str);
            return JsonResult.error("获取Openid异常");
        }
    }

    /** 获取用户手机号码 */
    public JsonResult getPhone(String params) {
        try {
            WxCustomDto customDto = JsonTools.getCustom(params);
            //System.out.println(params);
            String code = JsonTools.getJsonParam(params, "code");
            String enc = JsonTools.getJsonParam(params, "encryptedData"); //
            String iv = JsonTools.getJsonParam(params, "iv");
            String phone = "";
            if(code==null || "".equals(code)) {
                SessionKey sessionKey = sessionKeyDao.findByOpenid(customDto.getOpenid());
                if(sessionKey!=null) {
                    String res = MiniUtils.getPhone(enc, sessionKey.getSk(), iv);
    //                log.info(res);
                    phone = JsonTools.getJsonParam(res, "phoneNumber");
                }
            }

            if(phone==null || "".equals(phone)) { //如果没有处理则重新处理
                if(NormalTools.isNullOr(code, enc, iv)) {
                    throw new BusinessException(BusinessException.Code.PARAM_NULL, "code、encryptedData、iv三者均不能为空");
                }
                MiniConfig config = miniConfigDao.loadOne();
                if(config==null || NormalTools.isNullOr(config.getAppid(), config.getAppSecret())) {
                    throw new BusinessException(BusinessException.Code.CONFIG_NULL, "小程序未配置或Appid、AppSecret为空");
                }
                RestTemplate template = new RestTemplate();
                String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+config.getAppid()
                        +"&secret="+config.getAppSecret()+"&js_code="+code+"&grant_type=authorization_code";
                String str = template.getForObject(url, String.class);
                //System.out.println("====================================");
                //log.info(str);
                String sessionKey = JsonTools.getJsonParam(str, "session_key");
                String res = MiniUtils.getPhone(enc, sessionKey, iv);
                phone = JsonTools.getJsonParam(res, "phoneNumber");
                //log.info(res);
            }
            if(phone!=null && !"".equals(phone.trim())) { //如果有手机号码，则保存
                agentDao.updatePhone(phone, customDto.getCustomId()); //保存代理的手机号码
                customerDao.updatePhone(phone, customDto.getCustomId());
            }
            return JsonResult.success().set("phone", phone);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.success("绑定失败").set("phone", "");
        }
    }

    /** 添加或修改SessionKey */
    private void addOrUpdateSessionKey(Integer customId, String openid, String sk, String code) {
        SessionKey key = sessionKeyDao.findByOpenid(openid);
        if(key==null) {
            key = new SessionKey();
        }
        key.setCustomId(customId);
        key.setCode(code);
        key.setSk(sk);
        key.setOpenid(openid);
        sessionKeyDao.save(key);
    }

    @Autowired
    private ICustomerDao customerDao;
    @Autowired
    private QiniuTools qiniuTools;
    /** 处理小程序获取用户授权信息 */
    private synchronized Customer handlerCustomer(Customer customer) {
        Customer old = customerDao.findByOpenid(customer.getOpenid());
        String headimg = customer.getHeadImgUrl();
        if(customer.getHeadImgUrl()!=null && !"".equals(customer.getHeadImgUrl())) { //如果有头像
            headimg = qiniuTools.uploadCustomerHeadImg(headimg, "head-"+customer.getOpenid()+".jpg");
//            headimg = qiniuTools.uploadCustomerHeadImg(headimg, "head-"+(UUID.randomUUID().toString())+".jpg");

        }
        customer.setHeadImgUrl(headimg);
        Customer res = null;
        if(old==null) { //如果不存在
            customer.setFirstFollowDay(NormalTools.curDate());
            customer.setFirstFollowTime(NormalTools.curDatetime());
            customer.setFirstFollowLong(System.currentTimeMillis());
            customer.setFollowDay(NormalTools.curDate());
            customer.setFollowTime(NormalTools.curDatetime());
            customer.setFollowLong(System.currentTimeMillis());

            //设置推荐者信息
            customer.setInviterId(customer.getLeaderId());
            customer.setInviterNickname(customer.getLeaderNickname());
            customer.setInviterOpenid(customer.getLeaderOpenid());
            //设置推荐者信息

            customerDao.save(customer);
            res = customer;
        } else {
           // MyBeanUtils.copyProperties(customer, old, "id", "phone", "name", "agentId", "firstFollowDay", "firstFollowTime", "firstFollowLong");
            old.setFollowDay(NormalTools.curDate());
            old.setFollowTime(NormalTools.curDatetime());
            old.setFollowLong(System.currentTimeMillis());
            old.setHeadImgUrl(customer.getHeadImgUrl());
            old.setNickname(customer.getNickname());
            if(old.getInviterId()==null || old.getInviterId()<=0) {
                //设置推荐者信息
                old.setInviterId(customer.getLeaderId());
                old.setInviterNickname(customer.getLeaderNickname());
                old.setInviterOpenid(customer.getLeaderOpenid());
            }
            if(old.getLeaderId()==null || old.getLeaderId()<=0) {
                old.setLeaderOpenid(customer.getLeaderOpenid());
                old.setLeaderNickname(customer.getLeaderNickname());
                old.setLeaderId(customer.getLeaderId());
            }
            old.setSex(customer.getSex());
            customerDao.save(old);
            res = old;
        }
        rabbitNormalTools.updateData("couponTools", "handlerFirstFollowCoupon", res);

        ///初始化用户对应的代理信息
        rabbitNormalTools.updateData("agentTools", "initAgent", customer);
        return res;
    }
}
