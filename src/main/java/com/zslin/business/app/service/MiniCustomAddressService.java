package com.zslin.business.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.dao.ICustomAddressDao;
import com.zslin.business.model.CustomAddress;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.validate.ValidationDto;
import com.zslin.core.validate.ValidationTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MiniCustomAddressService {

    @Autowired
    private ICustomAddressDao customAddressDao;

    /**
     * 获取自己的收货地址
     * @param params
     * @return
     */
    public JsonResult list(String params) {
        WxCustomDto custom = JsonTools.getCustom(params);
        Sort sort = SimpleSortBuilder.generateSort("isDefault_d", "id_d");
        List<CustomAddress> list = customAddressDao.findByCustomId(custom.getCustomId(), sort);
        return JsonResult.success().set("addressList", list);
    }

    public JsonResult add(String params) {
        try {
            WxCustomDto custom = JsonTools.getCustom(params);
            CustomAddress address = JSONObject.toJavaObject(JSON.parseObject(params), CustomAddress.class);
            System.out.println(address);
            ValidationDto vd = ValidationTools.buildValidate(address);
            if(vd.isHasError()) { //如果有验证异常
                return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
            }
            if("1".equalsIgnoreCase(address.getIsDefault())) {
                customAddressDao.cleanDefault();
            }
            address.setOpenid(custom.getOpenid());
            address.setUnionid(custom.getUnionid());
            address.setNickname(custom.getNickname());
            address.setCustomId(custom.getCustomId());
            customAddressDao.save(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.success("保存成功");
    }

    /**
     * 获取地址
     * @param params
     * @return
     */
    public JsonResult loadOne(String params) {
        Integer id = JsonTools.getId(params);
        WxCustomDto custom = JsonTools.getCustom(params);
        CustomAddress address = customAddressDao.findByCustomIdAndId(custom.getCustomId(), id);
        return JsonResult.success().set("address", address);
    }

    public JsonResult delete(String params) {
        Integer id = JsonTools.getId(params);
        WxCustomDto custom = JsonTools.getCustom(params);
        customAddressDao.deleteByCustomIdAndId(custom.getCustomId(), id);
        return JsonResult.success("删除成功");
    }
}
