package com.zslin.core.tools;

import com.zslin.core.dao.IBaseTaskDao;
import com.zslin.core.model.BaseTask;
import com.zslin.core.tasker.CronTaskRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class AutoRunTools {

    @Autowired
    private IBaseTaskDao baseTaskDao;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    /**
     * 自动启动所有应该运行的任务
     */
    @PostConstruct
    public void handlerTask() {
        List<BaseTask> taskList = baseTaskDao.listByStatus("1"); //获取所有运行的任务
        for(BaseTask task : taskList) {cronTaskRegistrar.addTask(task);}
    }
}
