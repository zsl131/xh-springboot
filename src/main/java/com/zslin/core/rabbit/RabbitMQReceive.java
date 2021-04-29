package com.zslin.core.rabbit;

import com.zslin.business.dao.ICustomerDao;
import com.zslin.business.dao.IShoppingBasketDao;
import com.zslin.business.mini.dao.ICustomSubscribeDao;
import com.zslin.business.mini.dao.ISubscribeMessageDao;
import com.zslin.business.mini.dto.MsgDto;
import com.zslin.business.mini.dto.PushMsgRabbitDto;
import com.zslin.business.mini.model.CustomSubscribe;
import com.zslin.business.mini.model.SubscribeMessage;
import com.zslin.business.mini.tools.PushMessageTools;
import com.zslin.business.model.Customer;
import com.zslin.business.model.ShoppingBasket;
import com.zslin.business.wx.dto.SendMessageDto;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.core.common.NormalTools;
import com.zslin.core.qiniu.tools.QiniuTools;
import com.zslin.core.tools.MyBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Component
@RabbitListener(queues = RabbitMQConfig.DIRECT_QUEUE) //监听的队列名称 TestDirectQueue
@Slf4j
public class RabbitMQReceive implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Autowired
    private TemplateMessageTools templateMessageTools;

    /** 处理模板消息 */
    @RabbitHandler
    public void handlerSendMessage(SendMessageDto dto) {
//        System.out.println("==============================");
//        System.out.println(dto);
        templateMessageTools.sendMessageByDto(dto);
    }

    /*@Autowired
    private IUserDao userDao;*/

    @RabbitHandler
    public void process(Map testMessage) {
        String res = "DirectReceiver消费者收到消息  : " + testMessage.toString();
//        System.out.println(res);
        log.info(res);
    }

    /*@RabbitHandler
    public void addUser(User user) {
        userDao.save(user);
        log.info("添加用户信息： "+ user.toString());
    }*/

    @Autowired
    private ICustomerDao customerDao;
    @Autowired
    private QiniuTools qiniuTools;

    @Autowired
    private IShoppingBasketDao shoppingBasketDao;

    @RabbitHandler
    public void handlerShoppingBasket(ShoppingBasket basket) {
        ShoppingBasket old = shoppingBasketDao.findByProIdAndSpecsIdAndOpenid(basket.getProId(), basket.getSpecsId(), basket.getOpenid());
        if(old!=null) {
            old.setAmount(old.getAmount() + basket.getAmount());
            old.setUpdateDay(NormalTools.curDate());
            old.setUpdateTime(NormalTools.curDatetime());
            old.setUpdateLong(System.currentTimeMillis());
            shoppingBasketDao.save(old);
        } else {
            basket.setCreateDay(NormalTools.curDate());
            basket.setCreateTime(NormalTools.curDatetime());
            basket.setCreateLong(System.currentTimeMillis());

            basket.setUpdateDay(NormalTools.curDate());
            basket.setUpdateTime(NormalTools.curDatetime());
            basket.setUpdateLong(System.currentTimeMillis());

            shoppingBasketDao.save(basket);
        }
    }

    /** 相对通用的处理方法 */
    @RabbitHandler
    public void handlerUpdate(RabbitUpdateDto dto) {
        //log.info("处理数据对象 {} ", dto.toString());
        try {
            List<Object> params = dto.getParams();
            Object obj = getApplicationContext().getBean(dto.getBeanName());
            Method method ;
            boolean hasParams = false;
            if(params==null || params.size()<=0) {
                method = obj.getClass().getMethod(dto.getMethodName());
            } else {
                hasParams = true;
                Class [] paramClz = new Class[params.size()];
                int index = 0;
                for(Object o : params) {
                    paramClz[index++] = o.getClass();
                }
                method = obj.getClass().getMethod(dto.getMethodName(), paramClz);
            }
            if(hasParams) {
                method.invoke(obj, params.toArray());
            } else {
                method.invoke(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@RabbitHandler
    public void handlerPlusProductCount(Integer proId) {
        //System.out.println("-------------->"+proId);
        productDao.plusReadCount(1, proId);
    }*/

    /** 处理小程序获取用户授权信息 */
    @RabbitHandler
    public void handlerCustomer(Customer customer) {
        Customer old = customerDao.findByOpenid(customer.getOpenid());
        String headimg = customer.getHeadImgUrl();
        if(customer.getHeadImgUrl()!=null && !"".equals(customer.getHeadImgUrl())) { //如果有头像
            headimg = qiniuTools.uploadCustomerHeadImg(headimg, customer.getOpenid()+".jpg");
        }
        customer.setHeadImgUrl(headimg);
        if(old==null) { //如果不存在
            customer.setFirstFollowDay(NormalTools.curDate());
            customer.setFirstFollowTime(NormalTools.curDatetime());
            customer.setFirstFollowLong(System.currentTimeMillis());
            customer.setFollowDay(NormalTools.curDate());
            customer.setFollowTime(NormalTools.curDatetime());
            customer.setFollowLong(System.currentTimeMillis());
            customerDao.save(customer);
        } else {
            MyBeanUtils.copyProperties(customer, old, "id", "firstFollowDay", "firstFollowTime", "firstFollowLong");
            customer.setFollowDay(NormalTools.curDate());
            customer.setFollowTime(NormalTools.curDatetime());
            customer.setFollowLong(System.currentTimeMillis());
            customerDao.save(old);
        }
    }

    /////订阅消息处理

    @Autowired
    private ISubscribeMessageDao subscribeMessageDao;

    @Autowired
    private ICustomSubscribeDao customSubscribeDao;

    @Autowired
    private PushMessageTools pushMessageTools;

    /**
     * 处理推送订阅消息
     * @param dto
     */
    @RabbitHandler
    public void handlePushMessage(PushMsgRabbitDto dto) {
        //log.info(dto.toString());
        SubscribeMessage sm = subscribeMessageDao.findBySn(dto.getTempSn()); //获取模板
        if(sm==null || sm.getTempId()==null || "".equals(sm.getTempId())) {return;} //如果不存在，直接返回

        //log.info("111111111111");
        CustomSubscribe cs = customSubscribeDao.findByCustomOpenidAndMessageId(dto.getToUser(), sm.getId());
        if(cs==null || !"1".equals(cs.getStatus())) {return;}//如果不存在或未订阅，直接返回

        //log.info("22222222222");
        MsgDto[] msgDtos = buildContent(sm.getContent(), dto.getContent());
        if(msgDtos==null) {return; } //如果没有参数则直接返回

        //log.info("3333333333333");
        pushMessageTools.push(dto.getToUser(), sm.getTempId(), dto.getPage(), msgDtos);
    }

    private MsgDto[] buildContent(String conKeys, List<String> conValues) {
        String [] keys = conKeys.split("_");
        if(keys.length>conValues.size()) {return null;} //如果实际值小于需要值则返回空

        MsgDto[] result = new MsgDto[keys.length];
        for(int i=0;i<keys.length;i++) {
            result[i] = new MsgDto(keys[i], conValues.get(i));
        }
        return result;
    }

    /////订阅消息处理
}
