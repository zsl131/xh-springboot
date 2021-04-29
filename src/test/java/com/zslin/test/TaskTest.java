package com.zslin.test;

import com.zslin.business.timer.OrdersTimer;
import com.zslin.core.model.BaseTask;
import com.zslin.core.tasker.CronTaskRegistrar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "zsl")
public class TaskTest {

    @Autowired
    CronTaskRegistrar cronTaskRegistrar;

    @Autowired
    private OrdersTimer ordersTimer;

    @Test
    public void testRun() {
        ordersTimer.confirmOrders();
    }

    @Test
    public void testList() {
        List<BaseTask> list = cronTaskRegistrar.listTask();
        for(BaseTask bt : list) {
            System.out.println(bt);
        }
    }

    @Test
    public void addTask() throws Exception {
        BaseTask task = new BaseTask();
        task.setPeriod(10l);
        task.setTaskUuid(UUID.randomUUID().toString());
        task.setBeanName("testService");
        task.setMethodName("handler");
        task.setType("2");
        task.setParams("sssssssssss");
        task.setStartTime("2019-12-20 18:55:20");
        cronTaskRegistrar.addTask(task);

        // 便于观察
        Thread.sleep(3000000);
    }

    @Test
    public void testTask() throws InterruptedException {
//        SchedulingRunnable task = new SchedulingRunnable("noParamTask","demoTask", "taskNoParams", null, "0/10 * * * * ?");
//        cronTaskRegistrar.addCronTask(task);

        // 便于观察
        Thread.sleep(3000000);
    }

    @Test
    public void remove1() {
        cronTaskRegistrar.removeByUuid("noParamTask");
    }

    @Test
    public void remove2() {
        cronTaskRegistrar.removeByUuid("hasParamTask");
    }

    @Test
    public void show() {
        cronTaskRegistrar.show();
    }
}
