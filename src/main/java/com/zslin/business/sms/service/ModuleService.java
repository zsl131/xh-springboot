package com.zslin.business.sms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.sms.dao.IModuleDao;
import com.zslin.business.sms.model.Module;
import com.zslin.business.sms.tools.SmsTools;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.MyBeanUtils;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Created by zsl on 2018/10/9.
 */
@Service
@AdminAuth(name = "短信模板管理", psn = "短信管理", url = "/admin/module", type = "1", orderNum = 3)
public class ModuleService {

    @Autowired
    private IModuleDao moduleDao;

    @Autowired
    private SmsTools smsTools;

    public JsonResult list(String params) {
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<Module> res = moduleDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList()),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        return JsonResult.getInstance().set("size", (int) res.getTotalElements()).set("data", res.getContent());
    }

    public JsonResult loadOne(String params) {
        try {
//            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            Integer id = JsonTools.getId(params);
            Module obj = moduleDao.findOne(id);
            return JsonResult.succ(obj);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    public JsonResult addOrUpdate(String params) {
        try {
            Module obj = JSONObject.toJavaObject(JSON.parseObject(params), Module.class);
            if(obj.getId()!=null && obj.getId()>0) {
                Module m = moduleDao.findOne(obj.getId());
                MyBeanUtils.copyProperties(obj, m, "id");
                moduleDao.save(m);
                return JsonResult.succ(m);
            } else {
                try {
                    Integer iid = smsTools.addModule(obj.getSign(), obj.getContent());
                    obj.setIid(iid);
                } catch (Exception e) {
                    String code = e.getMessage();
                    String msg = e.getMessage();
//                module.setStatus(code);
                    obj.setStatus("1".equals(code)?"0":code);
                    obj.setReason(msg);
                }

                moduleDao.save(obj);
                return JsonResult.succ(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    public JsonResult delete(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
//            moduleDao.delete(id);
            Module module = moduleDao.findOne(id);
            if(module.getIid()!=null && module.getIid()>0) {
                smsTools.deleteModule(module.getIid()); //同时删除接口中心的短信模板
            }
            moduleDao.deleteById(id);
            return JsonResult.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }

    public JsonResult synch(String params) {
        try {
            smsTools.listModules(); //获取列表的同时进行同步操作
            return JsonResult.success("同步成功");
        } catch (Exception e) {
            return JsonResult.error(e.getMessage());
        }
    }

    //提交短信模板到中心
    public JsonResult send(String params) {
        try {
            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
            Module m = moduleDao.findOne(id);
            String res = "1";
            if(!"1".equals(m.getStatus())) {
                try {
                    Integer iid = smsTools.addModule(m.getSign(), m.getContent());
                    m.setIid(iid);
                } catch (Exception e) {
                    String code = e.getMessage();
                    String msg = e.getMessage();
                    if(!"-3".equals(code)) {
                        m.setStatus("1".equals(code)?"0":code);
                        m.setReason(msg);
                        res = code;
                    }
                }
                moduleDao.save(m);
            }
            return JsonResult.success("提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }
}
