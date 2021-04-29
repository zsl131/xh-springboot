package com.zslin.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dao.IBaseTaskDao;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.model.BaseTask;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tasker.BeanCheckTools;
import com.zslin.core.tasker.CronTaskRegistrar;
import com.zslin.core.tasker.TaskDto;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.MyBeanUtils;
import com.zslin.core.tools.QueryTools;
import com.zslin.core.validate.ValidationDto;
import com.zslin.core.validate.ValidationTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AdminAuth(name = "任务管理", psn = "系统管理", orderNum = 4, type = "1", url = "/admin/basic/baseTask")
@Explain(name = "系统任务管理", notes = "系统任务管理")
@Slf4j
public class AdminBaseTaskService {

    @Autowired
    private IBaseTaskDao baseTaskDao;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @Autowired
    private BeanCheckTools beanCheckTools;

    @AdminAuth(name = "任务列表", orderNum = 1)
    @ExplainOperation(name = "任务列表", notes = "任务列表", params= {
            @ExplainParam(value = "page", name = "页码，从0开始，默认0", require = false, type = "int", example = "0"),
            @ExplainParam(value = "size", name = "每页条数，默认15答", require = false, type = "int", example = "15"),
            @ExplainParam(value = "sort", name = "排序，id_desc表示根据id降序", require = false, type = "String", example = "id_desc"),
            @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
    }, back = {
            @ExplainReturn(field = "size", type = "int", notes = "任务数量"),
            @ExplainReturn(field = "datas", type = "Object", notes = "任务数组对象")
    })
    public JsonResult list(String params) {
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<BaseTask> res = baseTaskDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("datas", res.getContent());
    }

    @AdminAuth(name = "添加任务", orderNum = 2)
    @ExplainOperation(name = "添加任务", notes = "添加任务信息", params = {
            @ExplainParam(value = "id", name = "对象id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "添加成功的对象信息")
    })
    @Transactional
    public JsonResult add(String params) {
        BaseTask obj = JSONObject.toJavaObject(JSON.parseObject(params), BaseTask.class);
        ValidationDto vd = ValidationTools.buildValidate(obj);
        if(vd.isHasError()) { //如果有验证异常
            return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
        }
        if(beanCheckTools.checkMethod(obj.getBeanName(), obj.getMethodName(), obj.getParams())) {
            log.info("{}.{} 任务可以使用", obj.getBeanName(), obj.getMethodName());
        }

        obj.setTaskUuid(UUID.randomUUID().toString());
        obj.setCreateDay(NormalTools.curDate());
        obj.setCreateTime(NormalTools.curDatetime());
        obj.setCreateLong(System.currentTimeMillis());
        baseTaskDao.save(obj);

        if("1".equals(obj.getStatus())) { //如果添加的时候status=1，则自动启动
            cronTaskRegistrar.addTask(obj); //添加到任务中
        }
        return JsonResult.success("发布新任务成功");
    }

    @AdminAuth(name = "修改任务", orderNum = 3)
    @ExplainOperation(name = "修改任务", notes = "修改任务信息", params = {
            @ExplainParam(value = "id", name = "对象id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "对应的对象信息")
    })
    @Transactional
    public JsonResult update(String params) {
        try {
            BaseTask o = JSONObject.toJavaObject(JSON.parseObject(params), BaseTask.class);
            ValidationDto vd = ValidationTools.buildValidate(o);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            BaseTask obj = baseTaskDao.findOne(o.getId());
            MyBeanUtils.copyProperties(o, obj, "id", "createDate", "createTime", "createLong", "createDay", "taskUuid", "sucCount", "errCount");
            baseTaskDao.save(obj);
            return JsonResult.success("任务已修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }
    }

    @ExplainOperation(name = "获取任务信息", notes = "通过ID获取任务对象", params = {
            @ExplainParam(value = "id", name = "对象ID", require = true, type = "int", example = "1")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    public JsonResult loadOne(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            BaseTask obj = baseTaskDao.findOne(id);
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "删除任务", orderNum = 4)
    @ExplainOperation(name = "删除任务", notes = "通过ID删除对象，不能删除系统管理员任务", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息"),
            @ExplainReturn(field = "flag", notes = "删除标识")
    })
    @Transactional
    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            BaseTask task = baseTaskDao.findOne(id);
            String taskUuid = task.getTaskUuid();
            TaskDto curTask = cronTaskRegistrar.findByUuid(taskUuid);
            if(curTask!=null) {
                return JsonResult.getInstance().failFlag("【"+task.getTaskDesc()+"】正在运行，请先停止后再删除！");
            } else {
                baseTaskDao.delete(task);
                return JsonResult.success("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "启动任务", orderNum = 4)
    @ExplainOperation(name = "启动任务", notes = "通过ID启动任务", params = {
            @ExplainParam(value = "id", name = "对象ID", type = "int", require = true, example = "1")
    }, back = {
            @ExplainReturn(field = "message", notes = "提示信息")
    })
    public JsonResult start(String params) {
        try {
            Integer id = JsonTools.getId(params);
            BaseTask task = baseTaskDao.findOne(id);
            cronTaskRegistrar.addTask(task); //添加到任务中
            baseTaskDao.updateStatus("1", id); //设置状态为启动

            return JsonResult.success("任务启动成功");

        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    @AdminAuth(name = "运行任务列表", orderNum = 1)
    @ExplainOperation(name = "运行任务列表", notes = "获取正在运行的任务列表", params= {
            @ExplainParam(value = "conditions", name = "筛选条件，id_eq:5表示id=5", require = false, type = "String", example = "id_eq:5")
    }, back = {
            @ExplainReturn(field = "size", notes = "数据数量"),
            @ExplainReturn(field = "datas", notes = "对象列表")
    })
    public JsonResult listRun(String params) {
//        Map<String, TaskDto> map = cronTaskRegistrar.list();
        List<BaseTask> list = cronTaskRegistrar.listTask();

        return JsonResult.getInstance().set("size", list.size()).set("runnerList", list);
    }

    @AdminAuth(name = "停止运行中的任务", orderNum = 1)
    @ExplainOperation(name = "停止运行中的任务", notes = "停止运行中的任务", params= {
            @ExplainParam(value = "taskUuid", name = "任务名称", require = true)
    }, back = {
            @ExplainReturn(field = "message", notes = "结果信息")
    })
    public JsonResult stopRun(String params) {
        try {
            String taskUuid = JsonTools.getJsonParam(params, "taskUuid");
            cronTaskRegistrar.removeByUuid(taskUuid);
            baseTaskDao.updateStatus("0", taskUuid); //修改状态为停止
            return JsonResult.success("任务停止成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(BusinessException.Code.DEFAULT_ERR_CODE, e.getMessage());
        }
    }
}
