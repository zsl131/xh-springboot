package com.zslin.test.controller;

import com.zslin.business.wx.tools.KfTools;
import com.zslin.core.common.NormalTools;
import com.zslin.core.model.BaseTask;
import com.zslin.core.tasker.CronTaskRegistrar;
import com.zslin.core.tasker.TaskDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "test")
public class TestController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @Autowired
    private KfTools kfTools;

    @GetMapping(value = "listAllKf")
    private String listAllKf() {
        String res = kfTools.listAll();
        return res;
    }

    @GetMapping(value = "send")
    public String send(String account, String nick, String nickname, String id, String touser, String content) {
        String res = kfTools.sendMsg(account, nick, nickname, id, touser, content);
        return res;
    }

    /*@GetMapping(value = "index")
    public String index(String msg, HttpServletRequest request) {
        String res = msg + request.getRequestedSessionId()+ "  test in TestController => " + NormalTools.curDatetime();
        log.info(res);
        return res;
    }

    @GetMapping(value = "addTask")
    public String addTask(String name, String beanName, String methodName, String params, String cron) {
        BaseTask task = new BaseTask();
        task.setTaskUuid(name);
        task.setBeanName(beanName);
        task.setMethodName(methodName);
        task.setParams(params);
        task.setType("3");
        task.setCron("0/10 * * * * ?");
//        task.
        cronTaskRegistrar.addTask(task);
        return "添加成功";
    }

    @GetMapping(value = "listTask")
    public String listTask() {
        Map<String, TaskDto> scheduledTasks = cronTaskRegistrar.list();
        StringBuffer sb = new StringBuffer();
        for(String key : scheduledTasks.keySet()) {
            BaseTask runner = scheduledTasks.get(key).getRunner();
            sb.append("------>key:::").append(key)
                    .append(", beanName:: ").append(runner.getBeanName())
                    .append(", methodName:: ").append(runner.getMethodName()).append("\n");
        }
        return sb.toString();
    }

    @GetMapping(value = "remove")
    public String remove(String name) {
        cronTaskRegistrar.removeByUuid(name);
        return "删除成功";
    }*/

    /*@GetMapping(value = "rabbit")
    public String rabbit(String msg, HttpServletRequest request) {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "test message, hello!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map=new HashMap<>();
        map.put("messageId",messageId);
        map.put("messageData",messageData);
        map.put("msg", "send msg is : "+msg);
        map.put("sessionId", request.getSession().getId());
        map.put("requestId", request.getRequestedSessionId());
        map.put("createTime",createTime);
        //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.DIRECT_ROUTING, map);
        return "ok";
    }*/

    /*@GetMapping(value = "addUser")
    public String addUser(String username) {
        User user = new User();
        user.setCreateTime(NormalTools.curDatetime());
        user.setIsAdmin("1");
        user.setNickname(username);
        user.setUsername(username);
        user.setPassword(username);
        user.setStatus("1");

//        userDao.save(user);
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.DIRECT_ROUTING, user);
        String res = " addUser => "+ user.toString();
        log.info(res);
        return res;
    }*/
}
