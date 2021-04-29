package com.zslin.core.rabbit;

import com.zslin.business.mini.dto.PushMsgRabbitDto;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过Rabbit进行修改操作的工具类
 */
@Component
public class RabbitNormalTools {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void updateData(String beanName, String methodName, Object ... params) {
        try {
            RabbitUpdateDto dto = new RabbitUpdateDto(beanName, methodName, params);
            rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.DIRECT_ROUTING, dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 推送订阅消息
     * @param tempSn 模板SN
     * @param toUser 接收者的Openid
     * @param page 点击时跳转的页面，可带参数
     * @param values 对应内容
     */
    public void pushMessage(String tempSn, String toUser, String page, String ... values) {
        try {
            PushMsgRabbitDto dto = new PushMsgRabbitDto();
            dto.setPage(page);
            dto.setTempSn(tempSn);
            dto.setToUser(toUser);
            List<String> content = new ArrayList<>();
            for(String val : values) {content.add(val);}
            dto.setContent(content);
            //System.out.println("---RabbitNormalTools--"+dto);
            rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.DIRECT_ROUTING, dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
